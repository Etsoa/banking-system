package com.example.centralizer.ejb;

import com.example.centralizer.dto.comptecourant.CompteCourant;
import com.example.centralizer.dto.comptecourant.Transaction;
import com.example.centralizer.dto.comptecourant.TypeTransaction;
import com.example.centralizer.dto.comptecourant.StatutTransaction;
import jakarta.ejb.Stateful;
import jakarta.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service EJB Stateful pour CompteCourant
 * Utilise JNDI lookup pour accéder aux services EJB distants du module comptecourant
 * Maintient la session utilisateur
 */
@Stateful
public class CompteCourantServiceImpl implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CompteCourantServiceImpl.class.getName());
    
    @EJB
    private AuthenticationServiceImpl authenticationService;
    
    private Object compteCourantServiceRemote;
    private Object transactionServiceRemote;    
    /**
     * Initialise les services EJB locaux via JNDI
     * Format JNDI pour EJB local dans WildFly: java:global/<module-name>/<bean-name>!<interface>
     */
    private void initializeRemoteServices() {
        if (compteCourantServiceRemote == null || transactionServiceRemote == null) {
            try {
                InitialContext ctx = new InitialContext();
                
                if (compteCourantServiceRemote == null) {
                    String jndiPath = "java:global/comptecourant/CompteCourantServiceBean!com.example.comptecourant.ejb.CompteCourantServiceRemote";
                    LOGGER.info("Lookup CompteCourantServiceRemote: " + jndiPath);
                    compteCourantServiceRemote = ctx.lookup(jndiPath);
                    LOGGER.info("CompteCourantServiceRemote initialisé avec succès");
                }
                
                if (transactionServiceRemote == null) {
                    String jndiPath = "java:global/comptecourant/TransactionServiceBean!com.example.comptecourant.ejb.TransactionServiceRemote";
                    LOGGER.info("Lookup TransactionServiceRemote: " + jndiPath);
                    transactionServiceRemote = ctx.lookup(jndiPath);
                    LOGGER.info("TransactionServiceRemote initialisé avec succès");
                }
            } catch (NamingException e) {
                LOGGER.severe("Erreur lors du lookup des services EJB distants: " + e.getMessage());
                throw new RuntimeException("Impossible de localiser les services EJB distants", e);
            }
        }
    }

    /**
     * Authentification sur le serveur de compte courant
     * Délègue à AuthenticationService
     */
    public com.example.centralizer.dto.comptecourant.LoginResponse login(String username, String password) {
        return authenticationService.login(username, password);
    }

    /**
     * Déconnexion du serveur
     * Délègue à AuthenticationService
     */
    public void logout() {
        authenticationService.logout();
    }
    
    /**
     * Récupère les informations de l'utilisateur actuellement connecté
     * Délègue à AuthenticationService
     */
    public com.example.centralizer.dto.comptecourant.LoginResponse getCurrentUser() {
        return authenticationService.getCurrentUser();
    }

    /**
     * Vérifie si l'utilisateur est connecté
     * Délègue à AuthenticationService
     */
    public boolean isAuthenticated() {
        return authenticationService.isAuthenticated();
    }
    /**
     * Retourne le rôle de l'utilisateur connecté
     * Délègue à AuthenticationService
     */
    public Integer getCurrentUserRoleId() {
        return authenticationService.getCurrentUserRoleId();
    }
    
    /**
     * Vérifie si l'utilisateur connecté a une permission spécifique
     * Délègue à AuthenticationService
     */
    public boolean aAutorisationPour(String nomTable, String nomAction) {
        return authenticationService.aAutorisationPour(nomTable, nomAction);
    }
    
    /**
     * Récupère la session complète de l'utilisateur connecté
     * Délègue à AuthenticationService
     */
    public com.example.centralizer.dto.comptecourant.SessionUtilisateur getSessionUtilisateur() {
        return authenticationService.getSessionUtilisateur();
    }
    
    /**
     * Récupère tous les comptes
     */
    public List<CompteCourant> getAllComptes() {
        try {
            initializeRemoteServices();
            
            // Utiliser la réflexion pour appeler getAllComptes()
            List<?> result = (List<?>) compteCourantServiceRemote.getClass()
                .getMethod("getAllComptes")
                .invoke(compteCourantServiceRemote);
            
            LOGGER.info("Comptes récupérés: " + (result != null ? result.size() : 0));
            return convertToCompteCourantDTOList((List<Object>) result);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des comptes: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Récupère un compte par son ID
     */
    public CompteCourant getCompteById(Integer idCompte) {
        try {
            initializeRemoteServices();
            
            // Utiliser la réflexion pour appeler getCompteById(Integer)
            Object result = compteCourantServiceRemote.getClass()
                .getMethod("getCompteById", Integer.class)
                .invoke(compteCourantServiceRemote, idCompte);
            
            LOGGER.info("Compte " + idCompte + " récupéré");
            return convertToCompteCourantDTO(result);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération du compte " + idCompte + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Crée un nouveau compte
     */
    public CompteCourant createCompte(BigDecimal soldeInitial) {
        try {
            initializeRemoteServices();
            
            if (soldeInitial == null) {
                soldeInitial = BigDecimal.ZERO;
            }
            
            // Créer un nouveau compte avec le solde initial
            CompteCourant nouveau = new CompteCourant();
            nouveau.setSolde(soldeInitial);
            
            // On doit utiliser la réflexion car le service attend com.example.comptecourant.models.CompteCourant
            // et non notre DTO com.example.centralizer.dto.comptecourant.CompteCourant
            Object result = compteCourantServiceRemote.getClass()
                .getMethod("createCompte", Object.class)
                .invoke(compteCourantServiceRemote, nouveau);
            
            LOGGER.info("Compte créé avec solde initial: " + soldeInitial);
            return convertToCompteCourantDTO(result);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la création de compte: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Demander un dépôt (crée une transaction en attente)
     * Règle métier: Les dépôts sont d'abord en attente, puis validés
     */
    public Transaction depot(Integer idCompte, BigDecimal montant) {
        try {
            if (idCompte == null || idCompte <= 0) {
                throw new IllegalArgumentException("ID du compte invalide");
            }
            if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Montant doit être positif");
            }
            
            initializeRemoteServices();
            
            // Créer une transaction en attente pour le dépôt
            Transaction transaction = new Transaction();
            transaction.setIdCompte(idCompte);
            transaction.setMontant(montant);
            transaction.setTypeTransaction(TypeTransaction.DEPOT);
            transaction.setDateTransaction(LocalDate.now());
            transaction.setStatutTransaction(StatutTransaction.EN_ATTENTE);
            
            // On doit utiliser la réflexion car le service attend com.example.comptecourant.models.Transaction
            Object result = transactionServiceRemote.getClass()
                .getMethod("demanderTransaction", Object.class)
                .invoke(transactionServiceRemote, transaction);
            
            LOGGER.info("Dépôt demandé sur le compte " + idCompte + " pour le montant " + montant);
            return convertToTransactionDTO(result);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors du dépôt: " + e.getMessage());
            return null;
        }
    }

    /**
     * Demander un retrait (crée une transaction en attente)
     * Règle métier: Les retraits sont d'abord en attente, puis validés
     */
    public Transaction retrait(Integer idCompte, BigDecimal montant) {
        try {
            if (idCompte == null || idCompte <= 0) {
                throw new IllegalArgumentException("ID du compte invalide");
            }
            if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Montant doit être positif");
            }
            
            initializeRemoteServices();
            
            // Créer une transaction en attente pour le retrait
            Transaction transaction = new Transaction();
            transaction.setIdCompte(idCompte);
            transaction.setMontant(montant);
            transaction.setTypeTransaction(TypeTransaction.RETRAIT);
            transaction.setDateTransaction(LocalDate.now());
            transaction.setStatutTransaction(StatutTransaction.EN_ATTENTE);
            
            // Appeler le service pour demander la transaction
            Object result = transactionServiceRemote.getClass()
                .getMethod("demanderTransaction", Object.class)
                .invoke(transactionServiceRemote, transaction);
            
            LOGGER.info("Retrait demandé sur le compte " + idCompte + " pour le montant " + montant);
            return convertToTransactionDTO(result);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors du retrait: " + e.getMessage());
            return null;
        }
    }

    /**
     * Récupère les transactions d'un compte
     */
    public List<Transaction> getTransactionsByCompte(Integer idCompte) {
        try {
            initializeRemoteServices();
            
            // Utiliser la réflexion pour appeler getTransactionsByCompte(Integer)
            List<?> result = (List<?>) transactionServiceRemote.getClass()
                .getMethod("getTransactionsByCompte", Integer.class)
                .invoke(transactionServiceRemote, idCompte);
            
            LOGGER.info("Transactions du compte " + idCompte + " récupérées: " + (result != null ? result.size() : 0));
            return convertToTransactionDTOList((List<Object>) result);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des transactions: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Récupère toutes les transactions
     */
    public List<Transaction> getAllTransactions() {
        try {
            initializeRemoteServices();
            LOGGER.info("Tentative de récupération de toutes les transactions");
            
            // Utiliser la réflexion pour appeler getAllTransactions()
            List<?> result = (List<?>) transactionServiceRemote.getClass()
                .getMethod("getAllTransactions")
                .invoke(transactionServiceRemote);
            
            LOGGER.info("Nombre de transactions reçues du serveur: " + (result != null ? result.size() : "null"));
            return convertToTransactionDTOList((List<Object>) result);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des transactions: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Valider une transaction
     */
    public boolean validerTransaction(Integer idTransaction) {
        try {
            initializeRemoteServices();
            LOGGER.info("Validation de la transaction #" + idTransaction);
            
            // Utiliser la réflexion pour appeler validerTransaction(Integer, boolean)
            Object result = transactionServiceRemote.getClass()
                .getMethod("validerTransaction", Integer.class, boolean.class)
                .invoke(transactionServiceRemote, idTransaction, true);
            
            LOGGER.info("Transaction #" + idTransaction + " validée avec succès");
            return result != null;
        } catch (Exception e) {
            LOGGER.severe("Exception lors de la validation de la transaction: " + e.getClass().getName());
            LOGGER.severe("Message: " + e.getMessage());
            return false;
        }
    }

    /**
     * Refuser une transaction
     */
    public boolean refuserTransaction(Integer idTransaction) {
        try {
            initializeRemoteServices();
            LOGGER.info("Refus de la transaction #" + idTransaction);
            
            // Utiliser la réflexion pour appeler validerTransaction(Integer, boolean)
            Object result = transactionServiceRemote.getClass()
                .getMethod("validerTransaction", Integer.class, boolean.class)
                .invoke(transactionServiceRemote, idTransaction, false);
            
            LOGGER.info("Transaction #" + idTransaction + " refusée avec succès");
            return result != null;
        } catch (Exception e) {
            LOGGER.severe("Exception lors du refus de la transaction: " + e.getClass().getName());
            LOGGER.severe("Message: " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupérer les transactions en attente de validation
     */
    public List<Transaction> getTransactionsEnAttente() {
        try {
            initializeRemoteServices();
            
            // Utiliser la réflexion pour appeler getTransactionsEnAttente()
            List<?> result = (List<?>) transactionServiceRemote.getClass()
                .getMethod("getTransactionsEnAttente")
                .invoke(transactionServiceRemote);
            
            LOGGER.info("Transactions en attente récupérées: " + (result != null ? result.size() : 0));
            return convertToTransactionDTOList((List<Object>) result);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des transactions en attente: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Crée un dépôt (alias pour depot)
     */
    public boolean creerDepot(Integer idCompte, BigDecimal montant) {
        Transaction transaction = depot(idCompte, montant);
        return transaction != null;
    }
    
    /**
     * Crée un retrait (alias pour retrait)
     */
    public boolean creerRetrait(Integer idCompte, BigDecimal montant) {
        Transaction transaction = retrait(idCompte, montant);
        return transaction != null;
    }

    // ==================== Méthodes de conversion DTO ====================

    private CompteCourant convertToCompteCourantDTO(Object remote) {
        if (remote == null) return null;
        try {
            CompteCourant dto = new CompteCourant();
            Object idObj = remote.getClass().getMethod("getIdCompte").invoke(remote);
            Object soldeObj = remote.getClass().getMethod("getSolde").invoke(remote);
            
            if (idObj != null) dto.setIdCompte(((Number) idObj).intValue());
            if (soldeObj != null) dto.setSolde((BigDecimal) soldeObj);
            
            return dto;
        } catch (Exception e) {
            LOGGER.warning("Erreur lors de la conversion CompteCourant: " + e.getMessage());
            return null;
        }
    }

    private List<CompteCourant> convertToCompteCourantDTOList(List<Object> remoteList) {
        List<CompteCourant> dtoList = new ArrayList<>();
        if (remoteList != null) {
            for (Object remote : remoteList) {
                CompteCourant dto = convertToCompteCourantDTO(remote);
                if (dto != null) {
                    dtoList.add(dto);
                }
            }
        }
        return dtoList;
    }

    private Transaction convertToTransactionDTO(Object remote) {
        if (remote == null) return null;
        try {
            Transaction dto = new Transaction();
            Object idObj = remote.getClass().getMethod("getIdTransaction").invoke(remote);
            Object montantObj = remote.getClass().getMethod("getMontant").invoke(remote);
            Object dateObj = remote.getClass().getMethod("getDateTransaction").invoke(remote);
            Object compteObj = remote.getClass().getMethod("getIdCompte").invoke(remote);
            Object typeObj = remote.getClass().getMethod("getTypeTransaction").invoke(remote);
            Object statutObj = remote.getClass().getMethod("getStatutTransaction").invoke(remote);
            
            if (idObj != null) dto.setIdTransaction(((Number) idObj).intValue());
            if (montantObj != null) dto.setMontant((BigDecimal) montantObj);
            if (dateObj != null) dto.setDateTransaction((LocalDate) dateObj);
            if (compteObj != null) dto.setIdCompte(((Number) compteObj).intValue());
            if (typeObj != null) dto.setTypeTransaction(TypeTransaction.valueOf(typeObj.toString()));
            if (statutObj != null) dto.setStatutTransaction(StatutTransaction.valueOf(statutObj.toString()));
            
            return dto;
        } catch (Exception e) {
            LOGGER.warning("Erreur lors de la conversion Transaction: " + e.getMessage());
            return null;
        }
    }

    private List<Transaction> convertToTransactionDTOList(List<Object> remoteList) {
        List<Transaction> dtoList = new ArrayList<>();
        if (remoteList != null) {
            for (Object remote : remoteList) {
                Transaction dto = convertToTransactionDTO(remote);
                if (dto != null) {
                    dtoList.add(dto);
                }
            }
        }
        return dtoList;
    }
}