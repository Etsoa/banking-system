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
    private String sessionCookie; // JSESSIONID=value
    private boolean isAuthenticated = false;

    public CompteCourantServiceImpl() {
        this.client = ClientBuilder.newClient();
    }

    /**
     * Authentification sur le serveur de compte courant
     */
    public boolean login(String username, String password) {
        try {
            // Créer la requête de login
            String loginJson = String.format("{\"nomUtilisateur\":\"%s\",\"motDePasse\":\"%s\"}", 
                username, password);
            
            WebTarget target = client.target(SERVER_URL).path("auth/login");
            Response response = target.request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(loginJson));
            
            if (response.getStatus() == 200) {
                // Capturer le cookie de session
                Map<String, NewCookie> cookies = response.getCookies();
                if (cookies.containsKey("JSESSIONID")) {
                    NewCookie jsessionid = cookies.get("JSESSIONID");
                    this.sessionCookie = jsessionid.getName() + "=" + jsessionid.getValue();
                    this.isAuthenticated = true;
                    LOGGER.info("Authentification réussie pour: " + username + " (Cookie: " + this.sessionCookie + ")");
                    response.close();
                    return true;
                }
            }
            response.close();
            return false;
        } catch (Exception e) {
            LOGGER.severe("Erreur d'authentification: " + e.getMessage());
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
            transaction.setTypeTransaction(TypeTransaction.DEPOT);
            transaction.setDateTransaction(LocalDate.now());
            transaction.setStatutTransaction(StatutTransaction.EN_ATTENTE);
            
            WebTarget target = client.target(SERVER_URL).path("transactions/depot");
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
            transaction.setTypeTransaction(TypeTransaction.RETRAIT);
            transaction.setDateTransaction(LocalDate.now());
            transaction.setStatutTransaction(StatutTransaction.EN_ATTENTE);
            
            WebTarget target = client.target(SERVER_URL).path("transactions/retrait");
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
            WebTarget target = client.target(SERVER_URL).path("transactions");
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
    
    /**
     * Vérifie si l'utilisateur est connecté
     */
    public boolean isAuthenticated() {
        return isAuthenticated && sessionCookie != null;
    }
}
