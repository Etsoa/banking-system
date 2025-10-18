package com.example.centralizer.services;

import com.example.centralizer.models.compteDepotDTO.Transaction;
import com.example.centralizer.models.compteDepotDTO.TransactionRequest;
import com.example.centralizer.models.compteDepotDTO.Transfert;
import com.example.centralizer.models.compteDepotDTO.TransfertRequest;
import com.example.centralizer.models.compteDepotDTO.CompteRequest;
import com.example.centralizer.models.compteDepotDTO.TransactionsByCompteAndTypeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
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
            String url = serverUrl + "/transaction/by-compte";
            LOGGER.info("Appel POST vers: " + url);
            
            CompteRequest request = new CompteRequest(compteId);
            HttpEntity<CompteRequest> httpEntity = new HttpEntity<>(request);
            ResponseEntity<List<Transaction>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
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
            String url = serverUrl + "/transaction/by-compte-and-type";
            LOGGER.info("Appel POST vers: " + url);
            
            TransactionsByCompteAndTypeRequest request = new TransactionsByCompteAndTypeRequest(compteId, typeId);
            HttpEntity<TransactionsByCompteAndTypeRequest> httpEntity = new HttpEntity<>(request);
            ResponseEntity<List<Transaction>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
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
            String url = serverUrl + "/transaction";
            LOGGER.info("Appel POST vers: " + url);
            
            // Créer une TransactionRequest sans date pour l'envoi à l'API C#
            TransactionRequest request = new TransactionRequest(
                transaction.getMontant(),
                transaction.getIdTypeTransaction(),
                transaction.getIdCompte()
            );
            
            Transaction result = restTemplate.postForObject(url, request, Transaction.class);
            return result;
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la création de la transaction: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteDepot");
            return null;
        }
    }

    /**
     * Créer un transfert entre deux comptes dépôt avec date personnalisée
     */
    public Transfert createTransfert(String compteEnvoyeur, String compteReceveur, BigDecimal montant, LocalDateTime dateTransfert) {
        try {
            String url = serverUrl + "/transfert";
            LOGGER.info("Appel POST vers: " + url);
            
            TransfertRequest request = new TransfertRequest(compteEnvoyeur, compteReceveur, montant, dateTransfert);
            Transfert result = restTemplate.postForObject(url, request, Transfert.class);
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
            String url = serverUrl + "/transfert";
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
            String url = serverUrl + "/transfert/by-compte";
            LOGGER.info("Appel POST vers: " + url);
            
            CompteRequest request = new CompteRequest(compteId);
            HttpEntity<CompteRequest> httpEntity = new HttpEntity<>(request);
            ResponseEntity<List<Transfert>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
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