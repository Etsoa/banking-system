package com.example.centralizer.services;

import com.example.centralizer.models.compteCourantDTO.Transaction;
import com.example.centralizer.models.compteCourantDTO.TypeTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service pour communiquer avec le ServeurCompteCourant pour les transactions
 */
@Service
public class TransactionCompteCourantService {
    private static final Logger LOGGER = Logger.getLogger(TransactionCompteCourantService.class.getName());

    @Value("${serveur.comptecourant.url}")
    private String serverUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    /**
     * Récupère toutes les transactions d'un compte
     */
    public List<Transaction> getTransactionsByCompte(int compteId) {
        try {
            String baseUrl = serverUrl.replace("/compte-courant", "");
            String url = baseUrl + "/transactions/compte/" + compteId;
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<List<Transaction>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Transaction>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération des transactions du compte " + compteId + ": " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteCourant");
            return null;
        }
    }

    /**
     * Récupère les transactions d'un compte filtrées par type
     */
    public List<Transaction> getTransactionsByCompteAndType(int compteId, Integer typeId) {
        try {
            String baseUrl = serverUrl.replace("/compte-courant", "");
            String url = baseUrl + "/transactions/compte/" + compteId + "/type/" + typeId;
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<List<Transaction>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Transaction>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération des transactions filtrées du compte " + compteId + ": " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteCourant");
            return null;
        }
    }

    /**
     * Récupère tous les types de transaction
     */
    public List<TypeTransaction> getAllTypesTransaction() {
        try {
            String baseUrl = serverUrl.replace("/compte-courant", "");
            String url = baseUrl + "/transactions/types";
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<List<TypeTransaction>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TypeTransaction>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération des types de transaction: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteCourant");
            return null;
        }
    }

    /**
     * Créer une nouvelle transaction
     */
    public Transaction createTransaction(Transaction transaction) {
        try {
            String baseUrl = serverUrl.replace("/compte-courant", "");
            String url = baseUrl + "/transactions";
            LOGGER.info("Appel POST vers: " + url);
            
            Transaction result = restTemplate.postForObject(url, transaction, Transaction.class);
            return result;
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la création de la transaction: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteCourant");
            return null;
        }
    }
}