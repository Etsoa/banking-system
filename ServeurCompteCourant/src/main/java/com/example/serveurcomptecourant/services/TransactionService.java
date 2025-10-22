package com.example.serveurcomptecourant.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.serveurcomptecourant.exceptions.SecurityException;
import com.example.serveurcomptecourant.exceptions.TransactionException;
import com.example.serveurcomptecourant.models.CompteCourant;
import com.example.serveurcomptecourant.models.StatutTransaction;
import com.example.serveurcomptecourant.models.Transaction;
import com.example.serveurcomptecourant.repository.CompteCourantRepository;
import com.example.serveurcomptecourant.repository.TransactionRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class TransactionService {
    private static final Logger LOGGER = Logger.getLogger(TransactionService.class.getName());
    
    @EJB
    private TransactionRepository transactionRepository;
    
    @EJB
    private CompteCourantRepository compteRepository;
    
    private UtilisateurService utilisateurService;
    
    /**
     * Injecte le UtilisateurService de la session
     */
    public void setUtilisateurService(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    /**
     * Récupère toutes les transactions (nécessite autorisation)
     */
    public List<Transaction> getAllTransactions() throws SecurityException, TransactionException {
        utilisateurService.exigerAutorisation("transactions", "read");
        
        try {
            return transactionRepository.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de toutes les transactions", e);
            throw new TransactionException("Erreur lors de la récupération des transactions", e);
        }
    }

    /**
     * Récupère les transactions d'un compte (nécessite autorisation)
     */
    public List<Transaction> getTransactionsByCompte(Integer idCompte) throws SecurityException, TransactionException {
        utilisateurService.exigerAutorisation("transactions", "read");
        
        if (idCompte == null || idCompte <= 0) {
            throw new IllegalArgumentException("L'ID du compte est obligatoire");
        }
        
        try {
            // Vérifier que le compte existe
            CompteCourant compte = compteRepository.find(idCompte);
            if (compte == null) {
                throw new IllegalArgumentException("Compte introuvable avec ID: " + idCompte);
            }
            
            return transactionRepository.findByCompteId(idCompte);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transactions du compte " + idCompte, e);
            throw new TransactionException("Erreur lors de la récupération des transactions", e);
        }
    }

    /**
     * Récupère les transactions par statut (nécessite autorisation)
     */
    public List<Transaction> getTransactionsByStatut(StatutTransaction statutTransaction) throws SecurityException, TransactionException {
        utilisateurService.exigerAutorisation("transactions", "read");
        
        if (statutTransaction == null) {
            throw new IllegalArgumentException("Le statut de transaction est obligatoire");
        }
        
        try {
            return transactionRepository.findByStatutTransaction(statutTransaction);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transactions par statut {0}", new Object[]{statutTransaction, e});
            throw new TransactionException("Erreur lors de la récupération des transactions", e);
        }
    }

    /**
     * ÉTAPE 1: Crée une demande de transaction (en attente de validation)
     * Cette méthode crée une transaction avec le statut "en_attente"
     */
    public Transaction demanderTransaction(Transaction transaction) throws SecurityException, TransactionException {
        utilisateurService.exigerAutorisation("transactions", "create");
        
        if (transaction == null) {
            throw new IllegalArgumentException("Les données de la transaction sont obligatoires");
        }
        
        validateTransactionData(transaction);
        
        try {
            // Vérifier que le compte existe
            CompteCourant compte = compteRepository.find(transaction.getIdCompte());
            if (compte == null) {
                throw new IllegalArgumentException("Compte introuvable avec ID: " + transaction.getIdCompte());
            }
            
            // Définir la date si elle n'est pas spécifiée
            if (transaction.getDateTransaction() == null) {
                transaction.setDateTransaction(LocalDate.now());
            }
            
            // IMPORTANT: Toute nouvelle transaction commence en "en_attente"
            transaction.setStatutTransaction(StatutTransaction.en_attente);
            
            Transaction savedTransaction = transactionRepository.save(transaction);
            
            LOGGER.log(Level.INFO, "Demande de transaction créée par {0}: ID={1}, Compte={2}, Type={3}, Montant={4}", 
                new Object[]{
                    utilisateurService.getUtilisateurConnecte().getNomUtilisateur(),
                    savedTransaction.getIdTransaction(),
                    transaction.getIdCompte(),
                    transaction.getTypeTransaction(),
                    transaction.getMontant()
                });
            
            return savedTransaction;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de la demande de transaction", e);
            throw new TransactionException("Erreur lors de la création de la demande de transaction", e);
        }
    }

    /**
     * ÉTAPE 2: Valide une transaction en attente (réservé aux utilisateurs autorisés)
     * Cette méthode confirme ou refuse une transaction selon l'action demandée
     */
    public Transaction validerTransaction(Integer idTransaction, boolean approuver) throws SecurityException, TransactionException {
        utilisateurService.exigerAutorisation("transactions", "create");
        
        if (idTransaction == null || idTransaction <= 0) {
            throw new IllegalArgumentException("L'ID de la transaction est obligatoire");
        }
        
        try {
            Transaction transaction = transactionRepository.findById(idTransaction);
            if (transaction == null) {
                throw new TransactionException.TransactionNotFoundException(idTransaction);
            }
            
            if (transaction.getStatutTransaction() != StatutTransaction.en_attente) {
                throw new TransactionException.TransactionStatutInvalideException(
                    idTransaction, transaction.getStatutTransaction().toString(), "en_attente");
            }
            
            // Appliquer la validation
            StatutTransaction nouveauStatut = approuver 
                ? StatutTransaction.confirmee 
                : StatutTransaction.refusee;
            
            transaction.setStatutTransaction(nouveauStatut);
            
            // Si la transaction est approuvée, mettre à jour les soldes
            if (approuver) {
                CompteCourant compte = compteRepository.find(transaction.getIdCompte());
                if (compte == null) {
                    throw new TransactionException("Compte introuvable avec ID: " + transaction.getIdCompte());
                }
                
                BigDecimal nouveauSolde;
                if (transaction.getTypeTransaction() == com.example.serveurcomptecourant.models.TypeTransaction.depot) {
                    // Dépôt : ajouter le montant au solde
                    nouveauSolde = compte.getSolde().add(transaction.getMontant());
                } else {
                    // Retrait : soustraire le montant du solde
                    nouveauSolde = compte.getSolde().subtract(transaction.getMontant());
                    
                    // Vérifier que le solde ne devient pas négatif
                    if (nouveauSolde.compareTo(BigDecimal.ZERO) < 0) {
                        throw new TransactionException("Solde insuffisant pour effectuer le retrait");
                    }
                }
                
                compte.setSolde(nouveauSolde);
                compteRepository.save(compte);
                
                LOGGER.log(Level.INFO, "Solde du compte {0} mis à jour: {1} -> {2}", 
                    new Object[]{
                        compte.getIdCompte(),
                        compte.getSolde().subtract(transaction.getTypeTransaction() == com.example.serveurcomptecourant.models.TypeTransaction.depot 
                            ? transaction.getMontant() : transaction.getMontant().negate()),
                        nouveauSolde
                    });
            }
            
            Transaction savedTransaction = transactionRepository.save(transaction);
            
            String action = approuver ? "approuvée" : "refusée";
            LOGGER.log(Level.INFO, "Transaction {0} par {1}: ID={2}, Nouveau statut={3}", 
                new Object[]{
                    action,
                    utilisateurService.getUtilisateurConnecte().getNomUtilisateur(),
                    idTransaction,
                    nouveauStatut
                });
            
            return savedTransaction;
        } catch (IllegalArgumentException | TransactionException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la validation de la transaction {0}", new Object[]{idTransaction, e});
            throw new TransactionException("Erreur lors de la validation de la transaction", e);
        }
    }

    /**
     * Récupère les transactions en attente de validation (nécessite autorisation de validation)
     */
    public List<Transaction> getTransactionsEnAttente() throws SecurityException, TransactionException {
        utilisateurService.exigerAutorisation("transactions", "create");
        
        try {
            return transactionRepository.findByStatutTransaction(StatutTransaction.en_attente);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transactions en attente", e);
            throw new TransactionException("Erreur lors de la récupération des transactions en attente", e);
        }
    }

    /**
     * Validation des données de transaction
     */
    private void validateTransactionData(Transaction transaction) {
        if (transaction.getMontant() == null || transaction.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        
        if (transaction.getIdCompte() == null || transaction.getIdCompte() <= 0) {
            throw new IllegalArgumentException("L'ID du compte est obligatoire");
        }
        
        if (transaction.getTypeTransaction() == null) {
            throw new IllegalArgumentException("Le type de transaction est obligatoire");
        }
    }
}