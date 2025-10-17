package com.example.centralizer.services;

import com.example.centralizer.models.compteDepotDTO.Transaction;
import com.example.centralizer.models.compteDepotDTO.Transfert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service pour gérer les transactions des comptes dépôt
 * Communique avec le ServeurCompteDepot (.NET)
 */
@Service
public class TransactionCompteDepotService {
    private static final Logger LOGGER = Logger.getLogger(TransactionCompteDepotService.class.getName());

    @Value("${serveur.compteDepot.url}")
    private String serverUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    /**
     * Récupère toutes les transactions d'un compte
     */
    public List<Transaction> getTransactionsByCompte(String compteId) {
        try {
            String baseUrl = serverUrl.replace("/api/CompteDepot", "");
            String url = baseUrl + "/api/transaction/compte/" + compteId;
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<List<Transaction>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Transaction>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération des transactions: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteDepot");
            return null;
        }
    }

    /**
     * Récupère les transactions d'un compte par type
     */
    public List<Transaction> getTransactionsByCompteAndType(String compteId, Integer typeId) {
        try {
            String baseUrl = serverUrl.replace("/api/CompteDepot", "");
            String url = baseUrl + "/api/transaction/compte/" + compteId + "/type/" + typeId;
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<List<Transaction>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Transaction>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération des transactions par type: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteDepot");
            return null;
        }
    }

    /**
     * Créer une nouvelle transaction
     */
    public Transaction createTransaction(Transaction transaction) {
        try {
            String baseUrl = serverUrl.replace("/api/CompteDepot", "");
            String url = baseUrl + "/api/transaction";
            LOGGER.info("Appel POST vers: " + url);
            
            Transaction result = restTemplate.postForObject(url, transaction, Transaction.class);
            return result;
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la création de la transaction: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteDepot");
            return null;
        }
    }

    /**
     * Créer un transfert entre deux comptes dépôt
     */
    public Transfert createTransfert(String compteEnvoyeur, String compteReceveur, BigDecimal montant) {
        try {
            String baseUrl = serverUrl.replace("/api/CompteDepot", "");
            String url = baseUrl + "/api/transaction/transfert/" + compteEnvoyeur + "/" + compteReceveur + "/" + montant;
            LOGGER.info("Appel POST vers: " + url);
            
            Transfert result = restTemplate.postForObject(url, null, Transfert.class);
            return result;
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la création du transfert: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteDepot");
            return null;
        }
    }

    /**
     * Récupère tous les transferts
     */
    public List<Transfert> getAllTransferts() {
        try {
            String baseUrl = serverUrl.replace("/api/CompteDepot", "");
            String url = baseUrl + "/api/transfert";
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<List<Transfert>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Transfert>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération des transferts: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteDepot");
            return null;
        }
    }

    /**
     * Récupère les transferts d'un compte
     */
    public List<Transfert> getTransfertsByCompte(String compteId) {
        try {
            String baseUrl = serverUrl.replace("/api/CompteDepot", "");
            String url = baseUrl + "/api/transfert/compte/" + compteId;
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<List<Transfert>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Transfert>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération des transferts du compte " + compteId + ": " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteDepot");
            return null;
        }
    }
}