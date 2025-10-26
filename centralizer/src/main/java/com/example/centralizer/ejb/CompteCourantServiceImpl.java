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
import com.example.comptecourant.ejb.TransactionServiceRemote;
import com.example.comptecourant.ejb.CompteCourantServiceRemote;

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
    
    private CompteCourantServiceRemote compteCourantServiceRemote;
    private TransactionServiceRemote transactionServiceRemote;    
    /**
     * Initialise les services EJB distants via JNDI
     * Format JNDI pour EJB distant: ejb:<app-name>/<module-name>/<bean-name>!<interface>
     */
    private void initializeRemoteServices() {
        if (compteCourantServiceRemote == null || transactionServiceRemote == null) {
            try {
                InitialContext ctx = new InitialContext();
                
                if (compteCourantServiceRemote == null) {
                    String jndiPath = "java:jboss/exported/comptecourant/CompteCourantServiceBean!com.example.comptecourant.ejb.CompteCourantServiceRemote";
                    LOGGER.info("Lookup CompteCourantServiceRemote local: " + jndiPath);
                    compteCourantServiceRemote = (CompteCourantServiceRemote) ctx.lookup(jndiPath);
                    LOGGER.info("CompteCourantServiceRemote local initialisé avec succès");
                }
                
                if (transactionServiceRemote == null) {
                    String jndiPath = "java:jboss/exported/comptecourant/TransactionServiceBean!com.example.comptecourant.ejb.TransactionServiceRemote";
                    LOGGER.info("Lookup TransactionServiceRemote local: " + jndiPath);
                    transactionServiceRemote = (TransactionServiceRemote) ctx.lookup(jndiPath);
                    LOGGER.info("TransactionServiceRemote local initialisé avec succès");
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
            
            List<CompteCourant> result = compteCourantServiceRemote.getAllComptes();
            
            LOGGER.info("Comptes récupérés: " + (result != null ? result.size() : 0));
            return result;
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
            
            CompteCourant result = compteCourantServiceRemote.getCompteById(idCompte);
            
            LOGGER.info("Compte " + idCompte + " récupéré");
            return result;
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
            
            CompteCourant result = compteCourantServiceRemote.createCompte(nouveau);
            
            LOGGER.info("Compte créé avec solde initial: " + soldeInitial);
            return result;
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
            transaction.setTypeTransaction(TypeTransaction.depot);
            transaction.setDateTransaction(LocalDate.now());
            transaction.setStatutTransaction(StatutTransaction.en_attente);
            
            Transaction result = transactionServiceRemote.demanderTransaction(transaction);
            
            LOGGER.info("Dépôt demandé sur le compte " + idCompte + " pour le montant " + montant);
            return result;
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
            transaction.setTypeTransaction(TypeTransaction.retrait);
            transaction.setDateTransaction(LocalDate.now());
            transaction.setStatutTransaction(StatutTransaction.en_attente);
            
            // Appeler le service pour demander la transaction
            Transaction result = transactionServiceRemote.demanderTransaction(transaction);
            
            LOGGER.info("Retrait demandé sur le compte " + idCompte + " pour le montant " + montant);
            return result;
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des transactions: " + e.getMessage());
            return null;
        }
    }

    /**
     * Récupère les transactions d'un compte
     */
    public List<Transaction> getTransactionsByCompte(Integer idCompte) {
        try {
            initializeRemoteServices();
            
            List<Transaction> result = transactionServiceRemote.getTransactionsByCompte(idCompte);
            
            LOGGER.info("Transactions du compte " + idCompte + " récupérées: " + (result != null ? result.size() : 0));
            return result;
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
            
            List<Transaction> result = transactionServiceRemote.getAllTransactions();
            
            LOGGER.info("Nombre de transactions reçues du serveur: " + (result != null ? result.size() : "null"));
            return result;
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
            
            Transaction result = transactionServiceRemote.validerTransaction(idTransaction, true);
            
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
            
            Transaction result = transactionServiceRemote.validerTransaction(idTransaction, false);
            
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
            
            List<Transaction> result = transactionServiceRemote.getTransactionsEnAttente();
            
            LOGGER.info("Transactions en attente récupérées: " + (result != null ? result.size() : 0));
            return result;
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
}