package com.example.serveurcomptecourant.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.serveurcomptecourant.exceptions.SecurityException;
import com.example.serveurcomptecourant.exceptions.TransactionException;
import com.example.serveurcomptecourant.models.CompteCourant;
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
    
    @EJB
    private UtilisateurService utilisateurService;

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
     * Récupère une transaction par ID (nécessite autorisation)
     */
    public Transaction getTransactionById(Integer id) throws SecurityException, TransactionException {
        utilisateurService.exigerAutorisation("transactions", "read");
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID de la transaction est obligatoire");
        }
        
        try {
            Transaction transaction = transactionRepository.findById(id);
            if (transaction == null) {
                throw new TransactionException.TransactionNotFoundException(id);
            }
            return transaction;
        } catch (TransactionException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de la transaction {0}", new Object[]{id, e});
            throw new TransactionException("Erreur lors de la récupération de la transaction", e);
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
     * Récupère les transactions d'un compte par type (nécessite autorisation)
     */
    public List<Transaction> getTransactionsByCompteAndType(Integer idCompte, Transaction.TypeTransaction typeTransaction) throws SecurityException, TransactionException {
        utilisateurService.exigerAutorisation("transactions", "read");
        
        if (idCompte == null || idCompte <= 0) {
            throw new IllegalArgumentException("L'ID du compte est obligatoire");
        }
        if (typeTransaction == null) {
            throw new IllegalArgumentException("Le type de transaction est obligatoire");
        }
        
        try {
            // Vérifier que le compte existe
            CompteCourant compte = compteRepository.find(idCompte);
            if (compte == null) {
                throw new IllegalArgumentException("Compte introuvable avec ID: " + idCompte);
            }
            
            // Pour l'instant, filtrer par compte puis par type en Java (on pourrait optimiser avec une requête JPQL)
            return transactionRepository.findByCompteId(idCompte).stream()
                    .filter(t -> t.getTypeTransaction() == typeTransaction)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transactions du compte {0} et type {1}", new Object[]{idCompte, typeTransaction, e});
            throw new TransactionException("Erreur lors de la récupération des transactions", e);
        }
    }

    /**
     * Récupère les transactions par statut (nécessite autorisation)
     */
    public List<Transaction> getTransactionsByStatut(Transaction.StatutTransaction statutTransaction) throws SecurityException, TransactionException {
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
     * Récupère les transactions d'un compte par statut (nécessite autorisation)
     */
    public List<Transaction> getTransactionsByCompteAndStatut(Integer idCompte, Transaction.StatutTransaction statutTransaction) throws SecurityException, TransactionException {
        utilisateurService.exigerAutorisation("transactions", "read");
        
        if (idCompte == null || idCompte <= 0) {
            throw new IllegalArgumentException("L'ID du compte est obligatoire");
        }
        if (statutTransaction == null) {
            throw new IllegalArgumentException("Le statut de transaction est obligatoire");
        }
        
        try {
            // Vérifier que le compte existe
            CompteCourant compte = compteRepository.find(idCompte);
            if (compte == null) {
                throw new IllegalArgumentException("Compte introuvable avec ID: " + idCompte);
            }
            
            return transactionRepository.findByCompteIdAndStatut(idCompte, statutTransaction);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transactions du compte {0} et statut {1}", new Object[]{idCompte, statutTransaction, e});
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
            transaction.setStatutTransaction(Transaction.StatutTransaction.en_attente);
            
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
        utilisateurService.exigerAutorisation("transactions", "validate");
        
        if (idTransaction == null || idTransaction <= 0) {
            throw new IllegalArgumentException("L'ID de la transaction est obligatoire");
        }
        
        try {
            Transaction transaction = transactionRepository.findById(idTransaction);
            if (transaction == null) {
                throw new TransactionException.TransactionNotFoundException(idTransaction);
            }
            
            if (transaction.getStatutTransaction() != Transaction.StatutTransaction.en_attente) {
                throw new TransactionException.TransactionStatutInvalideException(
                    idTransaction, transaction.getStatutTransaction().toString(), "en_attente");
            }
            
            // Appliquer la validation
            Transaction.StatutTransaction nouveauStatut = approuver 
                ? Transaction.StatutTransaction.confirmee 
                : Transaction.StatutTransaction.refusee;
            
            transaction.setStatutTransaction(nouveauStatut);
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
     * Met à jour une transaction (nécessite autorisation)
     */
    public Transaction updateTransaction(Transaction transaction) throws SecurityException, TransactionException {
        utilisateurService.exigerAutorisation("transactions", "update");
        
        if (transaction == null || transaction.getIdTransaction() == null) {
            throw new IllegalArgumentException("La transaction et son ID sont obligatoires");
        }
        
        try {
            // Vérifier que la transaction existe
            Transaction existingTransaction = transactionRepository.findById(transaction.getIdTransaction());
            if (existingTransaction == null) {
                throw new TransactionException.TransactionNotFoundException(transaction.getIdTransaction());
            }
            
            // Empêcher la modification de transactions confirmées ou refusées
            if (existingTransaction.getStatutTransaction() != Transaction.StatutTransaction.en_attente) {
                throw new TransactionException.TransactionStatutInvalideException(
                    transaction.getIdTransaction(), 
                    existingTransaction.getStatutTransaction().toString(), 
                    "en_attente");
            }
            
            validateTransactionData(transaction);
            
            // Vérifier que le compte existe
            CompteCourant compte = compteRepository.find(transaction.getIdCompte());
            if (compte == null) {
                throw new IllegalArgumentException("Compte introuvable avec ID: " + transaction.getIdCompte());
            }
            
            // Maintenir le statut en_attente pour les modifications
            transaction.setStatutTransaction(Transaction.StatutTransaction.en_attente);
            
            Transaction savedTransaction = transactionRepository.save(transaction);
            
            LOGGER.log(Level.INFO, "Transaction modifiée par {0}: ID={1}", 
                new Object[]{
                    utilisateurService.getUtilisateurConnecte().getNomUtilisateur(),
                    transaction.getIdTransaction()
                });
            
            return savedTransaction;
        } catch (IllegalArgumentException | TransactionException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de la transaction {0}", new Object[]{transaction.getIdTransaction(), e});
            throw new TransactionException("Erreur lors de la mise à jour de la transaction", e);
        }
    }

    /**
     * Supprime une transaction (nécessite autorisation)
     */
    public void deleteTransaction(Integer id) throws SecurityException, TransactionException {
        utilisateurService.exigerAutorisation("transactions", "delete");
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID de la transaction est obligatoire");
        }
        
        try {
            Transaction transaction = transactionRepository.findById(id);
            if (transaction == null) {
                throw new TransactionException.TransactionNotFoundException(id);
            }
            
            // Empêcher la suppression de transactions confirmées
            if (transaction.getStatutTransaction() == Transaction.StatutTransaction.confirmee) {
                throw new TransactionException.TransactionNonModifiableException(id);
            }
            
            transactionRepository.delete(transaction);
            
            LOGGER.log(Level.INFO, "Transaction supprimée par {0}: ID={1}", 
                new Object[]{
                    utilisateurService.getUtilisateurConnecte().getNomUtilisateur(),
                    id
                });
        } catch (IllegalArgumentException | TransactionException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de la transaction {0}", new Object[]{id, e});
            throw new TransactionException("Erreur lors de la suppression de la transaction", e);
        }
    }

    /**
     * Confirme une transaction en attente (utilise la logique de validation)
     */
    public Transaction confirmerTransaction(Integer id) throws SecurityException, TransactionException {
        return validerTransaction(id, true);
    }

    /**
     * Refuse une transaction en attente (utilise la logique de validation)
     */
    public Transaction refuserTransaction(Integer id) throws SecurityException, TransactionException {
        return validerTransaction(id, false);
    }

    /**
     * Récupère les transactions en attente de validation (nécessite autorisation de validation)
     */
    public List<Transaction> getTransactionsEnAttente() throws SecurityException, TransactionException {
        utilisateurService.exigerAutorisation("transactions", "validate");
        
        try {
            return transactionRepository.findByStatutTransaction(Transaction.StatutTransaction.en_attente);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transactions en attente", e);
            throw new TransactionException("Erreur lors de la récupération des transactions en attente", e);
        }
    }

    /**
     * Vérifie si l'utilisateur connecté peut valider les transactions
     */
    public boolean peutValiderTransactions() {
        try {
            return utilisateurService.aAutorisationPour("transactions", "validate");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la vérification des autorisations de validation", e);
            return false;
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