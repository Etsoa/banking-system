package com.example.centralizer.ejb;

import com.example.centralizer.dto.CompteCourant;
import com.example.centralizer.dto.Transaction;
import com.example.centralizer.dto.TypeTransaction;
import com.example.centralizer.dto.StatutTransaction;
import jakarta.ejb.Stateful;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Service EJB Stateful pour CompteCourant
 * Maintient la session utilisateur avec cookies JSESSIONID
 */
@Stateful
public class CompteCourantServiceImpl {
    private static final Logger LOGGER = Logger.getLogger(CompteCourantServiceImpl.class.getName());
    
    private static final String SERVER_URL = "http://localhost:8080/comptecourant/api";
    
    private Client client;
    private String sessionCookie;
    private boolean isAuthenticated = false;

    public CompteCourantServiceImpl() {
        this.client = ClientBuilder.newClient();
    }

    /**
     * Authentification sur le serveur de compte courant
     */
    public boolean login(String username, String password) {
        try {
            LOGGER.info("Tentative d'authentification pour: " + username + " sur " + SERVER_URL + "/auth/login");
            
            // Créer la requête de login
            String loginJson = String.format("{\"nomUtilisateur\":\"%s\",\"motDePasse\":\"%s\"}", 
                username, password);
            
            LOGGER.info("Payload JSON: " + loginJson);
            
            WebTarget target = client.target(SERVER_URL).path("auth/login");
            Response response = target.request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(loginJson));
            
            int status = response.getStatus();
            LOGGER.info("Réponse du serveur: Status " + status);
            
            if (status == 200) {
                // Capturer le cookie de session
                Map<String, NewCookie> cookies = response.getCookies();
                LOGGER.info("Cookies reçus: " + cookies.keySet());
                
                if (cookies.containsKey("JSESSIONID")) {
                    NewCookie jsessionid = cookies.get("JSESSIONID");
                    this.sessionCookie = jsessionid.getName() + "=" + jsessionid.getValue();
                    this.isAuthenticated = true;
                    LOGGER.info("Authentification réussie pour: " + username + " (Cookie: " + this.sessionCookie + ")");
                    response.close();
                    return true;
                } else {
                    LOGGER.warning("Pas de cookie JSESSIONID dans la réponse");
                }
            } else {
                String errorBody = response.readEntity(String.class);
                LOGGER.warning("Échec d'authentification - Status: " + status + ", Body: " + errorBody);
            }
            
            response.close();
            return false;
        } catch (Exception e) {
            LOGGER.severe("Erreur d'authentification: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Déconnexion du serveur
     */
    public void logout() {
        try {
            if (isAuthenticated && sessionCookie != null) {
                WebTarget target = client.target(SERVER_URL).path("auth/logout");
                Response response = target.request(MediaType.APPLICATION_JSON)
                        .header("Cookie", sessionCookie)
                        .post(Entity.text(""));
                response.close();
                
                this.isAuthenticated = false;
                this.sessionCookie = null;
                LOGGER.info("Déconnexion réussie");
            }
        } catch (Exception e) {
            LOGGER.warning("Erreur lors de la déconnexion: " + e.getMessage());
            this.isAuthenticated = false;
            this.sessionCookie = null;
        }
    }
    
    /**
     * Récupère les informations de l'utilisateur actuellement connecté
     */
    public com.example.centralizer.dto.LoginResponse getCurrentUser() {
        try {
            WebTarget target = client.target(SERVER_URL).path("auth/current");
            Response response = target.request(MediaType.APPLICATION_JSON)
                    .header("Cookie", sessionCookie)
                    .get();
            
            if (response.getStatus() == 200) {
                // Le serveur renvoie un objet Utilisateur
                String jsonResponse = response.readEntity(String.class);
                response.close();
                
                // Parser manuellement le JSON pour extraire idUtilisateur et nomUtilisateur
                // Format attendu: {"idUtilisateur":1,"nomUtilisateur":"admin",...}
                Integer userId = extractUserId(jsonResponse);
                String username = extractUsername(jsonResponse);
                
                com.example.centralizer.dto.LoginResponse loginResponse = 
                    new com.example.centralizer.dto.LoginResponse(true, "Utilisateur récupéré");
                loginResponse.setIdUtilisateur(userId);
                loginResponse.setNomUtilisateur(username);
                return loginResponse;
            }
            
            response.close();
            return null;
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération de l'utilisateur courant: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Extrait l'ID utilisateur du JSON
     */
    private Integer extractUserId(String json) {
        try {
            String pattern = "\"idUtilisateur\"\\s*:\\s*(\\d+)";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                return Integer.parseInt(m.group(1));
            }
        } catch (Exception e) {
            LOGGER.warning("Impossible d'extraire idUtilisateur du JSON: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Extrait le nom d'utilisateur du JSON
     */
    private String extractUsername(String json) {
        try {
            String pattern = "\"nomUtilisateur\"\\s*:\\s*\"([^\"]+)\"";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception e) {
            LOGGER.warning("Impossible d'extraire nomUtilisateur du JSON: " + e.getMessage());
        }
        return null;
    }

    public List<CompteCourant> getAllComptes() {
        try {
            WebTarget target = client.target(SERVER_URL).path("comptes");
            Response response = target.request(MediaType.APPLICATION_JSON)
                    .header("Cookie", sessionCookie)
                    .get();
            
            if (response.getStatus() == 200) {
                List<CompteCourant> comptes = response.readEntity(new GenericType<List<CompteCourant>>() {});
                response.close();
                return comptes;
            }
            
            LOGGER.severe("Erreur lors de la récupération des comptes: " + response.getStatus());
            response.close();
            return new ArrayList<>();
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des comptes: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public CompteCourant getCompteById(Integer idCompte) {
        try {
            WebTarget target = client.target(SERVER_URL).path("comptes/" + idCompte);
            Response response = target.request(MediaType.APPLICATION_JSON)
                    .header("Cookie", sessionCookie)
                    .get();
            
            if (response.getStatus() == 200) {
                CompteCourant compte = response.readEntity(CompteCourant.class);
                response.close();
                return compte;
            }
            
            response.close();
            return null;
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération du compte " + idCompte + ": " + e.getMessage());
            return null;
        }
    }

    public CompteCourant createCompte(BigDecimal soldeInitial) {
        try {
            CompteCourant compte = new CompteCourant(soldeInitial);
            
            WebTarget target = client.target(SERVER_URL).path("comptes");
            Response response = target.request(MediaType.APPLICATION_JSON)
                    .header("Cookie", sessionCookie)
                    .post(Entity.json(compte));
            
            if (response.getStatus() == 201 || response.getStatus() == 200) {
                CompteCourant created = response.readEntity(CompteCourant.class);
                response.close();
                return created;
            }
            
            response.close();
            return null;
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la création de compte: " + e.getMessage());
            return null;
        }
    }

    public Transaction depot(Integer idCompte, BigDecimal montant) {
        try {
            Transaction transaction = new Transaction();
            transaction.setIdCompte(idCompte);
            transaction.setMontant(montant);
            transaction.setTypeTransaction(TypeTransaction.depot);
            transaction.setDateTransaction(LocalDate.now());
            transaction.setStatutTransaction(StatutTransaction.en_attente);
            
            WebTarget target = client.target(SERVER_URL).path("transactions/demander");
            Response response = target.request(MediaType.APPLICATION_JSON)
                    .header("Cookie", sessionCookie)
                    .post(Entity.json(transaction));
            
            if (response.getStatus() == 200 || response.getStatus() == 201) {
                Transaction created = response.readEntity(Transaction.class);
                response.close();
                return created;
            }
            
            response.close();
            return null;
        } catch (Exception e) {
            LOGGER.severe("Erreur lors du dépôt: " + e.getMessage());
            return null;
        }
    }

    public Transaction retrait(Integer idCompte, BigDecimal montant) {
        try {
            Transaction transaction = new Transaction();
            transaction.setIdCompte(idCompte);
            transaction.setMontant(montant);
            transaction.setTypeTransaction(TypeTransaction.retrait);
            transaction.setDateTransaction(LocalDate.now());
            transaction.setStatutTransaction(StatutTransaction.en_attente);
            
            WebTarget target = client.target(SERVER_URL).path("transactions/demander");
            Response response = target.request(MediaType.APPLICATION_JSON)
                    .header("Cookie", sessionCookie)
                    .post(Entity.json(transaction));
            
            if (response.getStatus() == 200 || response.getStatus() == 201) {
                Transaction created = response.readEntity(Transaction.class);
                response.close();
                return created;
            }
            
            response.close();
            return null;
        } catch (Exception e) {
            LOGGER.severe("Erreur lors du retrait: " + e.getMessage());
            return null;
        }
    }

    public List<Transaction> getTransactionsByCompte(Integer idCompte) {
        try {
            WebTarget target = client.target(SERVER_URL).path("transactions/compte/" + idCompte);
            Response response = target.request(MediaType.APPLICATION_JSON)
                    .header("Cookie", sessionCookie)
                    .get();
            
            if (response.getStatus() == 200) {
                List<Transaction> transactions = response.readEntity(new GenericType<List<Transaction>>() {});
                response.close();
                return transactions;
            }
            
            response.close();
            return new ArrayList<>();
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des transactions: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Transaction> getAllTransactions() {
        try {
            LOGGER.info("Tentative de récupération de toutes les transactions");
            LOGGER.info("Cookie utilisé: " + sessionCookie);
            WebTarget target = client.target(SERVER_URL).path("transactions");
            Response response = target.request(MediaType.APPLICATION_JSON)
                    .header("Cookie", sessionCookie)
                    .get();
            
            LOGGER.info("Réponse getAllTransactions - Status: " + response.getStatus());
            
            if (response.getStatus() == 200) {
                List<Transaction> transactions = response.readEntity(new GenericType<List<Transaction>>() {});
                LOGGER.info("Nombre de transactions reçues du serveur: " + (transactions != null ? transactions.size() : "null"));
                response.close();
                return transactions;
            } else {
                String errorBody = response.readEntity(String.class);
                LOGGER.warning("Échec getAllTransactions - Status: " + response.getStatus() + ", Body: " + errorBody);
            }
            
            response.close();
            return new ArrayList<>();
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des transactions: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Valider une transaction
     */
    public boolean validerTransaction(Integer idTransaction) {
        try {
            LOGGER.info("=== DEBUT validerTransaction ===");
            LOGGER.info("ID Transaction: " + idTransaction);
            LOGGER.info("Cookie utilisé: " + sessionCookie);
            LOGGER.info("URL cible: " + SERVER_URL + "/transactions/" + idTransaction + "/valider");
            
            WebTarget target = client.target(SERVER_URL)
                    .path("transactions/" + idTransaction + "/valider");
            
            LOGGER.info("Envoi de la requête PUT...");
            Response response = target.request(MediaType.APPLICATION_JSON)
                    .header("Cookie", sessionCookie)
                    .put(Entity.json(""));
            
            int status = response.getStatus();
            LOGGER.info("Réponse reçue - Status: " + status);
            
            if (status != 200) {
                String errorBody = response.readEntity(String.class);
                LOGGER.warning("Erreur du serveur - Body: " + errorBody);
                response.close();
                return false;
            }
            
            boolean success = response.getStatus() == 200;
            response.close();
            
            if (success) {
                LOGGER.info("Transaction #" + idTransaction + " validée avec succès");
            }
            
            return success;
        } catch (Exception e) {
            LOGGER.severe("Exception lors de la validation de la transaction: " + e.getClass().getName());
            LOGGER.severe("Message: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Refuser une transaction
     */
    public boolean refuserTransaction(Integer idTransaction) {
        try {
            LOGGER.info("=== DEBUT refuserTransaction ===");
            LOGGER.info("ID Transaction: " + idTransaction);
            LOGGER.info("Cookie utilisé: " + sessionCookie);
            LOGGER.info("URL cible: " + SERVER_URL + "/transactions/" + idTransaction + "/refuser");
            
            WebTarget target = client.target(SERVER_URL)
                    .path("transactions/" + idTransaction + "/refuser");
            
            LOGGER.info("Envoi de la requête PUT...");
            Response response = target.request(MediaType.APPLICATION_JSON)
                    .header("Cookie", sessionCookie)
                    .put(Entity.json(""));
            
            int status = response.getStatus();
            LOGGER.info("Réponse reçue - Status: " + status);
            
            if (status != 200) {
                String errorBody = response.readEntity(String.class);
                LOGGER.warning("Erreur du serveur - Body: " + errorBody);
                response.close();
                return false;
            }
            
            boolean success = response.getStatus() == 200;
            response.close();
            
            if (success) {
                LOGGER.info("Transaction #" + idTransaction + " refusée avec succès");
            }
            
            return success;
        } catch (Exception e) {
            LOGGER.severe("Exception lors du refus de la transaction: " + e.getClass().getName());
            LOGGER.severe("Message: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Récupérer les transactions en attente de validation
     */
    public List<Transaction> getTransactionsEnAttente() {
        try {
            WebTarget target = client.target(SERVER_URL)
                    .path("transactions/statut/en_attente");
            
            Response response = target.request(MediaType.APPLICATION_JSON)
                    .header("Cookie", sessionCookie)
                    .get();
            
            if (response.getStatus() == 200) {
                List<Transaction> transactions = response.readEntity(new GenericType<List<Transaction>>() {});
                response.close();
                return transactions;
            }
            
            response.close();
            return new ArrayList<>();
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des transactions en attente: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Vérifie si l'utilisateur est connecté
     */
    public boolean isAuthenticated() {
        return isAuthenticated && sessionCookie != null;
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
