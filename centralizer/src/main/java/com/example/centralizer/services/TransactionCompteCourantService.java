package com.example.centralizer.services;

import com.example.centralizer.models.compteCourantDTO.Transaction;
import com.example.centralizer.models.compteCourantDTO.TypeTransaction;
import com.example.centralizer.models.compteCourantDTO.Transfert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    public List<Transaction> getTransactionsByCompte(String compteId) {
        try {
             
            String url =  serverUrl + "/transactions/compte/" + compteId;
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
    public List<Transaction> getTransactionsByCompteAndType(String compteId, Integer typeId) {
        try {
             
            String url =  serverUrl + "/transactions/compte/" + compteId + "/type/" + typeId;
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
             
            String url =  serverUrl + "/transactions/types";
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
             
            String url =  serverUrl + "/transactions";
            LOGGER.info("Appel POST vers: " + url);
            
            Transaction result = restTemplate.postForObject(url, transaction, Transaction.class);
            return result;
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la création de la transaction: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteCourant");
            return null;
        }
    }

    /**
     * Créer un transfert entre deux comptes avec date personnalisée
     */
    public Transfert createTransfert(String compteEnvoyeur, String compteReceveur, BigDecimal montant, LocalDateTime dateTransfert) {
        try {
            String url = serverUrl + "/transferts";
            LOGGER.info("Appel POST vers: " + url);
            
            // Créer l'objet transfert
            Transfert transfert = new Transfert();
            transfert.setEnvoyer(compteEnvoyeur);
            transfert.setReceveur(compteReceveur);
            transfert.setMontant(montant);
            // Convertir LocalDateTime en LocalDate
            transfert.setDateTransfert(dateTransfert.toLocalDate());
            
            Transfert result = restTemplate.postForObject(url, transfert, Transfert.class);
            return result;
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la création du transfert: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteCourant");
            return null;
        }
    }

    /**
     * Récupère tous les transferts
     */
    public List<Transfert> getAllTransferts() {
        try {
             
            String url =  serverUrl + "/transferts";
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
            exceptionHandlingService.handleServerException(e, "ServeurCompteCourant");
            return null;
        }
    }

    /**
     * Récupère les transferts d'un compte
     */
    public List<Transfert> getTransfertsByCompte(String compteId) {
        try {
             
            String url =  serverUrl + "/transferts/compte/" + compteId;
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
            exceptionHandlingService.handleServerException(e, "ServeurCompteCourant");
            return null;
        }
    }
}