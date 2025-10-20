package com.example.serveurcomptecourant.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.serveurcomptecourant.exceptions.CompteCourantBusinessException;
import com.example.serveurcomptecourant.exceptions.CompteCourantException;
import com.example.serveurcomptecourant.models.CompteCourant;
import com.example.serveurcomptecourant.models.Decouverte;
import com.example.serveurcomptecourant.models.Frais;
import com.example.serveurcomptecourant.models.Transaction;
import com.example.serveurcomptecourant.models.TransactionAvecFrais;
import com.example.serveurcomptecourant.models.Transfert;
import com.example.serveurcomptecourant.models.TypeTransaction;
import com.example.serveurcomptecourant.repository.CompteCourantRepository;
import com.example.serveurcomptecourant.repository.TransactionRepository;
import com.example.serveurcomptecourant.repository.TypeTransactionRepository;

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
    
    @EJB
    private DecouverteService decouverteService;
    
    @EJB
    private FraisService fraisService;

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
     * Récupère les transactions d'un compte avec les frais appliqués
     */
    public List<TransactionAvecFrais> getTransactionsByCompteAvecFrais(String compteId) throws CompteCourantException {
        try {
            if (compteId == null || compteId.trim().isEmpty()) {
                throw new CompteCourantBusinessException("L'ID du compte est obligatoire");
            }
            
            // Vérifier que le compte existe
            CompteCourant compte = compteRepository.find(compteId);
            if (compte == null) {
                throw new CompteCourantBusinessException.CompteNotFoundException(0L);
            }
            
            List<Transaction> transactions = transactionRepository.findByCompteId(compteId);
            return enrichirTransactionsAvecFrais(transactions);
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transactions avec frais du compte " + compteId, e);
            throw new CompteCourantException("Erreur lors de la récupération des transactions avec frais", e);
        }
    }

    /**
     * Récupère les transactions d'un compte par type avec les frais appliqués
     */
    public List<TransactionAvecFrais> getTransactionsByCompteAndTypeAvecFrais(String compteId, Integer typeId) throws CompteCourantException {
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
            
            List<Transaction> transactions = transactionRepository.findByCompteIdAndTypeTransaction(compteId, typeId);
            return enrichirTransactionsAvecFrais(transactions);
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transactions avec frais du compte " + compteId + " et type " + typeId, e);
            throw new CompteCourantException("Erreur lors de la récupération des transactions avec frais", e);
        }
    }

    /**
     * Enrichit une liste de transactions avec les informations de frais
     */
    private List<TransactionAvecFrais> enrichirTransactionsAvecFrais(List<Transaction> transactions) {
        return transactions.stream()
            .map(this::enrichirTransactionAvecFrais)
            .toList();
    }

    /**
     * Enrichit une transaction avec les informations de frais
     */
    private TransactionAvecFrais enrichirTransactionAvecFrais(Transaction transaction) {
        try {
            // Récupérer le type de transaction
            TypeTransaction typeTransaction = typeTransactionRepository.findById(transaction.getIdTypeTransaction());
            
            // Vérifier si des frais s'appliquent à ce type de transaction
            if (typeTransaction != null && 
                ("Retrait".equalsIgnoreCase(typeTransaction.getLibelle()) || 
                 "Virement sortant".equalsIgnoreCase(typeTransaction.getLibelle()))) {
                
                // Récupérer les frais applicables
                LocalDateTime dateRef = transaction.getDateTransaction() != null ? 
                                      transaction.getDateTransaction() : LocalDateTime.now();
                
                Frais fraisApplicables = fraisService.findCurrentFrais(
                    typeTransaction.getLibelle(), 
                    transaction.getMontant(), 
                    dateRef);
                
                return new TransactionAvecFrais(transaction, typeTransaction, fraisApplicables);
            } else {
                return new TransactionAvecFrais(transaction, typeTransaction);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur lors de l'enrichissement de la transaction " + transaction.getId(), e);
            // En cas d'erreur, retourner la transaction sans frais
            TypeTransaction typeTransaction = null;
            try {
                typeTransaction = typeTransactionRepository.findById(transaction.getIdTypeTransaction());
            } catch (Exception ignored) {}
            return new TransactionAvecFrais(transaction, typeTransaction);
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
    public Transaction createTransaction(Transaction transaction, BigDecimal revenu) throws CompteCourantException {
        try {
            // Validation des données
            validateTransactionData(transaction);
            
            CompteCourant compte = compteRepository.find(transaction.getIdCompte());
            TypeTransaction typeTransaction = typeTransactionRepository.findById(transaction.getIdTypeTransaction());
            return executeTransaction(transaction, compte, typeTransaction, revenu);
            
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
    public Transfert createTransfert(String compteEnvoyeur, String compteReceveur, BigDecimal montant, LocalDateTime dateTransfert) throws CompteCourantException {
        try {
            // Vérifier que les comptes existent
            CompteCourant envoyeur = compteRepository.find(compteEnvoyeur);
            CompteCourant receveur = compteRepository.find(compteReceveur);
            
            // Récupérer les types de transaction pour virement
            TypeTransaction typeSortant = getTypeTransactionByLibelle("Virement sortant");
            TypeTransaction typeEntrant = getTypeTransactionByLibelle("Virement entrant");
            
            // Créer la transaction sortante (débit du compte envoyeur)
            Transaction transactionSortante = new Transaction();
            transactionSortante.setMontant(montant);
            transactionSortante.setIdCompte(compteEnvoyeur);
            transactionSortante.setIdTypeTransaction(typeSortant.getId());
            transactionSortante.setDateTransaction(dateTransfert);

            // Pour les transferts, pas besoin du revenu - on utilise null
            Transaction savedTransactionSortante = executeTransaction(transactionSortante, envoyeur, typeSortant, null);
            
            // Créer la transaction entrante (crédit du compte receveur)
            Transaction transactionEntrante = new Transaction();
            transactionEntrante.setMontant(montant);
            transactionEntrante.setIdCompte(compteReceveur);
            transactionEntrante.setIdTypeTransaction(typeEntrant.getId());
            transactionEntrante.setDateTransaction(dateTransfert);
            
            // Pour les crédits, pas besoin de vérifier le découvert, donc revenu = null
            Transaction savedTransactionEntrante = executeTransaction(transactionEntrante, receveur, typeEntrant, null);
            
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
    private Transaction executeTransaction(Transaction transaction, CompteCourant compte, TypeTransaction typeTransaction, BigDecimal revenu) throws CompteCourantBusinessException {
        // 1. Calculer le montant signé et le nouveau solde
        BigDecimal montantSigne = calculerMontantSigne(transaction.getMontant(), typeTransaction.getSigne());
        BigDecimal nouveauSolde = compte.getSolde().add(montantSigne);
        
        // 2. Vérifier les contraintes de découvert avant la transaction
        verifierContraintesDecouverts(compte, typeTransaction, nouveauSolde, revenu, transaction.getDateTransaction());
        
        // 3. Sauvegarder la transaction
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // 4. Calculer et appliquer les frais
        BigDecimal soldeApresTransactionEtFrais = appliquerFrais(compte, typeTransaction, transaction, nouveauSolde, revenu);
        
        // 5. Mettre à jour le solde du compte
        compte.setSolde(soldeApresTransactionEtFrais);
        compteRepository.save(compte);
        
        // 6. Créer l'historique de solde
        creerHistoriqueSolde(soldeApresTransactionEtFrais, transaction.getIdCompte(), savedTransaction.getId());
        
        return savedTransaction;
    }
    
    /**
     * Vérifie les contraintes de découvert pour la transaction
     */
    private void verifierContraintesDecouverts(CompteCourant compte, TypeTransaction typeTransaction, 
                                             BigDecimal nouveauSolde, BigDecimal revenu, LocalDateTime dateTransaction) 
                                             throws CompteCourantBusinessException {
        
        // Vérifier uniquement pour les débits
        if (nouveauSolde.compareTo(BigDecimal.ZERO) >= 0) {
            return; // Pas de découvert, pas de vérification nécessaire
        }
        
        // Si découvert non autorisé
        if (!compte.getDecouvert()) {
            throw new CompteCourantBusinessException("Solde insuffisant - découvert non autorisé");
        }
        
        // Si découvert autorisé, vérifier les limites pour les retraits uniquement
        if ("Retrait".equalsIgnoreCase(typeTransaction.getLibelle())) {
            verifierLimiteDecouvertPourRetrait(nouveauSolde, revenu, dateTransaction);
        } else {
            // Autres types de débits : découvert autorisé sans limite basée sur le revenu
            LOGGER.log(Level.INFO, "Débit (" + typeTransaction.getLibelle() + ") avec découvert autorisé");
        }
    }
    
    /**
     * Vérifie la limite de découvert pour un retrait
     */
    private void verifierLimiteDecouvertPourRetrait(BigDecimal nouveauSolde, BigDecimal revenu, LocalDateTime dateTransaction) 
                                                  throws CompteCourantBusinessException {
        if (revenu == null) {
            throw new CompteCourantBusinessException("Le revenu doit être spécifié pour un retrait avec découvert");
        }
        
        try {
            Decouverte decouverteActuelle = decouverteService.findCurrentDecouverte(revenu, dateTransaction);
            if (decouverteActuelle == null) {
                throw new CompteCourantBusinessException("Aucune limite de découvert trouvée pour le revenu: " + revenu);
            }
            
            BigDecimal limiteDecouverte = decouverteActuelle.getLimiteDecouverte();
            if (nouveauSolde.abs().compareTo(limiteDecouverte) > 0) {
                throw new CompteCourantBusinessException(
                    "Limite de découvert dépassée pour retrait. Limite autorisée: " + limiteDecouverte + " pour revenu: " + revenu
                );
            }
        } catch (CompteCourantException e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la vérification de la limite de découvert pour retrait", e);
            throw new CompteCourantBusinessException("Erreur lors de la vérification de la limite de découvert pour retrait");
        }
    }
    
    /**
     * Applique les frais pour les retraits et virements sortants
     */
    private BigDecimal appliquerFrais(CompteCourant compte, TypeTransaction typeTransaction, Transaction transaction, 
                                    BigDecimal nouveauSolde, BigDecimal revenu) throws CompteCourantBusinessException {
        
        // Les frais s'appliquent uniquement aux retraits et virements sortants
        if (!"Retrait".equalsIgnoreCase(typeTransaction.getLibelle()) && 
            !"Virement sortant".equalsIgnoreCase(typeTransaction.getLibelle())) {
            return nouveauSolde;
        }
        
        try {
            LocalDateTime dateRef = transaction.getDateTransaction() != null ? 
                                  transaction.getDateTransaction() : LocalDateTime.now();
            
            Frais fraisApplicables = fraisService.findCurrentFrais(typeTransaction.getLibelle(), transaction.getMontant(), dateRef);
            
            if (fraisApplicables == null) {
                return nouveauSolde; // Pas de frais applicables
            }
            
            BigDecimal montantFrais = BigDecimal.valueOf(fraisApplicables.getValeur());
            BigDecimal soldeApresTransactionEtFrais = nouveauSolde.subtract(montantFrais);
            
            // Vérifier que l'application des frais ne dépasse pas les limites
            verifierContraintesFrais(compte, typeTransaction, soldeApresTransactionEtFrais, montantFrais, revenu, transaction.getDateTransaction());
            
            LOGGER.log(Level.INFO, "Frais de " + montantFrais + " appliqués pour " + typeTransaction.getLibelle() + " de " + transaction.getMontant());
            return soldeApresTransactionEtFrais;
            
        } catch (CompteCourantException e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la récupération des frais pour " + typeTransaction.getLibelle(), e);
            return nouveauSolde; // Continue sans frais en cas d'erreur
        }
    }
    
    /**
     * Vérifie les contraintes liées aux frais
     */
    private void verifierContraintesFrais(CompteCourant compte, TypeTransaction typeTransaction, 
                                        BigDecimal soldeApresTransactionEtFrais, BigDecimal montantFrais, 
                                        BigDecimal revenu, LocalDateTime dateTransaction) throws CompteCourantBusinessException {
        
        // Si le solde après frais reste positif, pas de problème
        if (soldeApresTransactionEtFrais.compareTo(BigDecimal.ZERO) >= 0) {
            return;
        }
        
        // Si découvert non autorisé
        if (!compte.getDecouvert()) {
            throw new CompteCourantBusinessException("Solde insuffisant pour couvrir les frais de " + montantFrais + " - découvert non autorisé");
        }
        
        // Vérifier les limites pour les retraits uniquement
        if ("Retrait".equalsIgnoreCase(typeTransaction.getLibelle()) && revenu != null) {
            try {
                Decouverte decouverteActuelle = decouverteService.findCurrentDecouverte(revenu, dateTransaction);
                if (decouverteActuelle != null) {
                    BigDecimal limiteDecouverte = decouverteActuelle.getLimiteDecouverte();
                    if (soldeApresTransactionEtFrais.abs().compareTo(limiteDecouverte) > 0) {
                        throw new CompteCourantBusinessException(
                            "Limite de découvert dépassée avec frais. Solde après frais: " + soldeApresTransactionEtFrais + ", Limite: " + limiteDecouverte
                        );
                    }
                }
            } catch (CompteCourantException e) {
                LOGGER.log(Level.WARNING, "Erreur lors de la vérification de la limite de découvert avec frais", e);
                throw new CompteCourantBusinessException("Erreur lors de la vérification de la limite de découvert avec frais");
            }
        }
    }
    
    /**
     * Crée l'historique de solde
     */
    private void creerHistoriqueSolde(BigDecimal nouveauSolde, String compteId, Integer transactionId) {
        try {
            historiqueSoldeService.createHistoriqueSolde(nouveauSolde, compteId, transactionId);
        } catch (CompteCourantException e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la création de l'historique de solde", e);
        }
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