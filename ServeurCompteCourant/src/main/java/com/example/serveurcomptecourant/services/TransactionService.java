package com.example.serveurcomptecourant.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.serveurcomptecourant.exceptions.CompteCourantBusinessException;
import com.example.serveurcomptecourant.exceptions.CompteCourantException;
import com.example.serveurcomptecourant.models.CompteCourant;
import com.example.serveurcomptecourant.models.Transaction;
import com.example.serveurcomptecourant.models.TypeTransaction;
import com.example.serveurcomptecourant.models.Transfert;
import com.example.serveurcomptecourant.repository.TransactionRepository;
import com.example.serveurcomptecourant.repository.TypeTransactionRepository;
import com.example.serveurcomptecourant.repository.CompteCourantRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;

@Stateless
public class TransactionService {
    private static final Logger LOGGER = Logger.getLogger(TransactionService.class.getName());
    
    @EJB
    private TransactionRepository transactionRepository;
    
    @EJB
    private TypeTransactionRepository typeTransactionRepository;
    
    @EJB
    private CompteCourantRepository compteRepository;
    
    @EJB
    private HistoriqueSoldeService historiqueSoldeService;
    
    @EJB
    private TransfertService transfertService;

    /**
     * Récupère toutes les transactions
     */
    public List<Transaction> getAllTransactions() throws CompteCourantException {
        try {
            return transactionRepository.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de toutes les transactions", e);
            throw new CompteCourantException("Erreur lors de la récupération des transactions", e);
        }
    }

    /**
     * Récupère les transactions d'un compte
     */
    public List<Transaction> getTransactionsByCompte(String compteId) throws CompteCourantException {
        try {
            if (compteId == null || compteId.trim().isEmpty()) {
                throw new CompteCourantBusinessException("L'ID du compte est obligatoire");
            }
            
            // Vérifier que le compte existe
            CompteCourant compte = compteRepository.find(compteId);
            if (compte == null) {
                throw new CompteCourantBusinessException.CompteNotFoundException(0L);
            }
            
            return transactionRepository.findByCompteId(compteId);
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transactions du compte " + compteId, e);
            throw new CompteCourantException("Erreur lors de la récupération des transactions", e);
        }
    }

    /**
     * Récupère les transactions d'un compte par type
     */
    public List<Transaction> getTransactionsByCompteAndType(String compteId, Integer typeId) throws CompteCourantException {
        try {
            if (compteId == null || compteId.trim().isEmpty()) {
                throw new CompteCourantBusinessException("L'ID du compte est obligatoire");
            }
            if (typeId == null || typeId <= 0) {
                throw new CompteCourantBusinessException("L'ID du type de transaction est obligatoire");
            }
            
            // Vérifier que le compte existe
            CompteCourant compte = compteRepository.find(compteId);
            if (compte == null) {
                throw new CompteCourantBusinessException.CompteNotFoundException(0L);
            }
            
            // Vérifier que le type de transaction existe
            TypeTransaction typeTransaction = typeTransactionRepository.findById(typeId);
            if (typeTransaction == null) {
                throw new CompteCourantBusinessException("Type de transaction introuvable");
            }
            
            return transactionRepository.findByCompteIdAndTypeTransaction(compteId, typeId);
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transactions du compte " + compteId + " et type " + typeId, e);
            throw new CompteCourantException("Erreur lors de la récupération des transactions", e);
        }
    }

    /**
     * Récupère une transaction par ID
     */
    public Transaction getTransactionById(Integer id) throws CompteCourantException {
        try {
            if (id == null || id <= 0) {
                throw new CompteCourantBusinessException("L'ID de la transaction est obligatoire");
            }
            
            Transaction transaction = transactionRepository.findById(id);
            if (transaction == null) {
                throw new CompteCourantBusinessException("Transaction introuvable");
            }
            
            return transaction;
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de la transaction " + id, e);
            throw new CompteCourantException("Erreur lors de la récupération de la transaction", e);
        }
    }

    /**
     * Crée un dépôt ou retrait (transaction simple)
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Transaction createTransaction(Transaction transaction) throws CompteCourantException {
        try {
            // Validation des données
            validateTransactionData(transaction);
            
            // Vérifier que le compte existe
            CompteCourant compte = compteRepository.find(transaction.getIdCompte());
            if (compte == null) {
                throw new CompteCourantBusinessException.CompteNotFoundException(0L);
            }
            
            // Vérifier que le type de transaction existe
            TypeTransaction typeTransaction = typeTransactionRepository.findById(transaction.getIdTypeTransaction());
            if (typeTransaction == null) {
                throw new CompteCourantBusinessException("Type de transaction introuvable");
            }
            
            // Vérifier que c'est bien un dépôt ou retrait (pas de virement)
            String libelle = typeTransaction.getLibelle().toLowerCase();
            if (libelle.contains("virement")) {
                throw new CompteCourantBusinessException("Utilisez createTransfert() pour les virements");
            }
            
            return executeTransaction(transaction, compte, typeTransaction);
            
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de la transaction", e);
            throw new CompteCourantException("Erreur lors de la création de la transaction", e);
        }
    }

    /**
     * Crée un transfert entre deux comptes (2 transactions + 1 transfert)
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Transfert createTransfert(String compteEnvoyeur, String compteReceveur, BigDecimal montant) throws CompteCourantException {
        try {
            // Validation des paramètres
            if (compteEnvoyeur == null || compteEnvoyeur.trim().isEmpty()) {
                throw new CompteCourantBusinessException("Le compte envoyeur est obligatoire");
            }
            if (compteReceveur == null || compteReceveur.trim().isEmpty()) {
                throw new CompteCourantBusinessException("Le compte receveur est obligatoire");
            }
            if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
                throw new CompteCourantBusinessException.MontantInvalideException(
                    montant != null ? montant.doubleValue() : 0.0
                );
            }
            if (compteEnvoyeur.equals(compteReceveur)) {
                throw new CompteCourantBusinessException("Le compte envoyeur et receveur doivent être différents");
            }
            
            // Vérifier que les comptes existent
            CompteCourant envoyeur = compteRepository.find(compteEnvoyeur);
            CompteCourant receveur = compteRepository.find(compteReceveur);
            
            if (envoyeur == null) {
                throw new CompteCourantBusinessException("Compte envoyeur introuvable");
            }
            if (receveur == null) {
                throw new CompteCourantBusinessException("Compte receveur introuvable");
            }
            
            // Récupérer les types de transaction pour virement
            TypeTransaction typeSortant = getTypeTransactionByLibelle("Virement sortant");
            TypeTransaction typeEntrant = getTypeTransactionByLibelle("Virement entrant");
            
            // Créer la transaction sortante (débit du compte envoyeur)
            Transaction transactionSortante = new Transaction();
            transactionSortante.setMontant(montant);
            transactionSortante.setIdCompte(compteEnvoyeur);
            transactionSortante.setIdTypeTransaction(typeSortant.getId());
            
            Transaction savedTransactionSortante = executeTransaction(transactionSortante, envoyeur, typeSortant);
            
            // Créer la transaction entrante (crédit du compte receveur)
            Transaction transactionEntrante = new Transaction();
            transactionEntrante.setMontant(montant);
            transactionEntrante.setIdCompte(compteReceveur);
            transactionEntrante.setIdTypeTransaction(typeEntrant.getId());
            
            Transaction savedTransactionEntrante = executeTransaction(transactionEntrante, receveur, typeEntrant);
            
            // Déléguer la création du transfert au service dédié
            return transfertService.createTransfert(compteEnvoyeur, compteReceveur, montant, 
                                                  savedTransactionSortante, savedTransactionEntrante);
            
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création du transfert", e);
            throw new CompteCourantException("Erreur lors de la création du transfert", e);
        }
    }

    /**
     * Récupère tous les types de transaction
     */
    public List<TypeTransaction> getAllTypesTransaction() throws CompteCourantException {
        try {
            return typeTransactionRepository.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des types de transaction", e);
            throw new CompteCourantException("Erreur lors de la récupération des types de transaction", e);
        }
    }

    /**
     * Récupère les types de transaction actifs
     */
    public List<TypeTransaction> getTypesTransactionActifs() throws CompteCourantException {
        try {
            return typeTransactionRepository.findActifs();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des types de transaction actifs", e);
            throw new CompteCourantException("Erreur lors de la récupération des types de transaction", e);
        }
    }

    /**
     * Délègue au TransfertService
     */
    public List<Transfert> getAllTransferts() throws CompteCourantException {
        return transfertService.getAllTransferts();
    }

    /**
     * Délègue au TransfertService
     */
    public List<Transfert> getTransfertsByCompte(String compteId) throws CompteCourantException {
        return transfertService.getTransfertsByCompte(compteId);
    }

    /**
     * Supprime une transaction (avec remise à jour du solde)
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteTransaction(Integer id) throws CompteCourantException {
        try {
            Transaction transaction = getTransactionById(id);
            
            // Récupérer le compte
            CompteCourant compte = compteRepository.find(transaction.getIdCompte());
            if (compte == null) {
                throw new CompteCourantBusinessException.CompteNotFoundException(0L);
            }
            
            // Récupérer le type de transaction
            TypeTransaction typeTransaction = typeTransactionRepository.findById(transaction.getIdTypeTransaction());
            if (typeTransaction == null) {
                throw new CompteCourantBusinessException("Type de transaction introuvable");
            }
            
            // Annuler l'effet de la transaction sur le solde
            BigDecimal montantSigne;
            try {
                montantSigne = calculerMontantSigne(transaction.getMontant(), typeTransaction.getSigne());
            } catch (CompteCourantBusinessException e) {
                throw e;
            }
            BigDecimal nouveauSolde = compte.getSolde().subtract(montantSigne);
            
            // Mettre à jour le solde du compte
            compte.setSolde(nouveauSolde);
            compteRepository.save(compte);
            
            // Supprimer la transaction
            transactionRepository.delete(id);
            
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de la transaction " + id, e);
            throw new CompteCourantException("Erreur lors de la suppression de la transaction", e);
        }
    }

    /**
     * Validation des données de transaction
     */
    private void validateTransactionData(Transaction transaction) throws CompteCourantBusinessException {
        if (transaction == null) {
            throw new CompteCourantBusinessException("Les données de la transaction sont obligatoires");
        }
        
        if (transaction.getMontant() == null || transaction.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CompteCourantBusinessException.MontantInvalideException(
                transaction.getMontant() != null ? transaction.getMontant().doubleValue() : 0.0
            );
        }
        
        if (transaction.getIdCompte() == null || transaction.getIdCompte().trim().isEmpty()) {
            throw new CompteCourantBusinessException("L'ID du compte est obligatoire");
        }
        
        if (transaction.getIdTypeTransaction() == null || transaction.getIdTypeTransaction() <= 0) {
            throw new CompteCourantBusinessException("Le type de transaction est obligatoire");
        }
    }

    /**
     * Exécute une transaction (logique commune dépôt/retrait/virement)
     */
    private Transaction executeTransaction(Transaction transaction, CompteCourant compte, TypeTransaction typeTransaction) throws CompteCourantBusinessException {
        // Calculer le nouveau solde
        BigDecimal montantSigne = calculerMontantSigne(transaction.getMontant(), typeTransaction.getSigne());
        BigDecimal nouveauSolde = compte.getSolde().add(montantSigne);
        
        // Vérifier le découvert autorisé (uniquement pour les débits)
        if (montantSigne.compareTo(BigDecimal.ZERO) < 0) {
            if (!compte.getDecouvert() && nouveauSolde.compareTo(BigDecimal.ZERO) < 0) {
                throw new CompteCourantBusinessException("Solde insuffisant - découvert non autorisé");
            }
        }
        
        // Sauvegarder la transaction
        transaction.setDateTransaction(LocalDateTime.now());
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Mettre à jour le solde du compte
        compte.setSolde(nouveauSolde);
        compteRepository.save(compte);
        
        // Déléguer la création de l'historique au service dédié
        try {
            historiqueSoldeService.createHistoriqueSolde(nouveauSolde, transaction.getIdCompte(), savedTransaction.getId());
        } catch (CompteCourantException e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la création de l'historique de solde", e);
            // Ne pas faire échouer la transaction pour un problème d'historique
        }
        
        return savedTransaction;
    }

    /**
     * Récupère un type de transaction par son libellé
     */
    private TypeTransaction getTypeTransactionByLibelle(String libelle) throws CompteCourantBusinessException {
        try {
            List<TypeTransaction> types = typeTransactionRepository.findAll();
            return types.stream()
                .filter(type -> type.getLibelle().equalsIgnoreCase(libelle))
                .findFirst()
                .orElseThrow(() -> new CompteCourantBusinessException("Type de transaction '" + libelle + "' introuvable"));
        } catch (Exception e) {
            throw new CompteCourantBusinessException("Erreur lors de la recherche du type de transaction: " + libelle);
        }
    }

    /**
     * Calcule le montant signé selon le type de transaction
     */
    private BigDecimal calculerMontantSigne(BigDecimal montant, String signe) throws CompteCourantBusinessException {
        switch (signe) {
            case "+":
                return montant;
            case "-":
                return montant.negate();
            default:
                throw new CompteCourantBusinessException("Signe de transaction invalide: " + signe);
        }
    }
}