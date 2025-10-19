package com.example.centralizer.services;

import com.example.centralizer.models.pretDTO.Pret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Service pour communiquer avec le ServeurPret
 * Gère automatiquement les exceptions provenant du serveur
 */
@Service
public class PretService {
    private static final Logger LOGGER = Logger.getLogger(PretService.class.getName());

    @Value("${serveur.pret.url}")
    private String serverUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    /**
     * Récupère tous les prêts
     */
    public List<Pret> getAllPrets() {
        try {
            String url = serverUrl;
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<List<Pret>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Pret>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération des prêts: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurPret");
            return null;
        }
    }

    /**
     * Récupère les prêts d'un client spécifique
     */
    public List<Pret> getPretsByClientId(Integer clientId) {
        try {
            String url = serverUrl + "/client/" + clientId;
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<List<Pret>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Pret>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération des prêts du client " + clientId + ": " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurPret");
            return null;
        }
    }

    /**
     * Récupère les prêts d'un client spécifique (version String pour compatibilité)
     */
    public List<Pret> getPretsByClientId(String clientId) {
        try {
            Integer id = Integer.valueOf(clientId);
            return getPretsByClientId(id);
        } catch (NumberFormatException e) {
            LOGGER.severe("ID client invalide: " + clientId);
            return null;
        }
    }

    /**
     * Crée un nouveau prêt (version simple)
     */
    public Pret createPret(Pret pret) {
        try {
            String url = serverUrl;
            LOGGER.info("Appel POST vers: " + url);
            
            ResponseEntity<Pret> response = restTemplate.postForEntity(url, pret, Pret.class);
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la création du prêt: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurPret");
            return null;
        }
    }

    /**
     * Crée un prêt complet avec validation et amortissement
     */
    public Pret createPretComplet(Integer clientId, BigDecimal montant, BigDecimal revenu, Integer dureeMois, 
                                 Integer modaliteId, Integer typeRemboursementId) {
        try {
            String url = serverUrl + "/complet";
            LOGGER.info("Appel POST vers: " + url);
            
            // Création d'un objet pour les paramètres
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("clientId", clientId);
            requestBody.put("montant", montant);
            requestBody.put("revenu", revenu);
            requestBody.put("dureeMois", dureeMois);
            requestBody.put("modaliteId", modaliteId);
            requestBody.put("typeRemboursementId", typeRemboursementId);
            
            ResponseEntity<Pret> response = restTemplate.postForEntity(url, requestBody, Pret.class);
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la création du prêt complet: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurPret");
            return null;
        }
    }

    /**
     * Récupère un prêt par son ID
     */
    public Pret getPretById(Long id) {
        try {
            String url = serverUrl + "/" + id;
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<Pret> response = restTemplate.getForEntity(url, Pret.class);
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération du prêt " + id + ": " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurPret");
            return null;
        }
    }

    /**
     * Récupère les informations du prochain remboursement à effectuer
     */
    public Map<String, Object> getInfosProchainRemboursement(Long pretId) {
        try {
            String url = serverUrl + "/" + pretId + "/prochain-remboursement";
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération des informations de remboursement: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurPret");
            return new HashMap<>();
        }
    }

    /**
     * Récupère l'historique des remboursements d'un prêt
     */
    public List<Map<String, Object>> getHistoriqueRemboursements(Long pretId) {
        try {
            String url = serverUrl + "/" + pretId + "/remboursements";
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération de l'historique des remboursements: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurPret");
            return null;
        }
    }

    /**
     * Effectue un paiement de remboursement
     */
    public Map<String, Object> effectuerRemboursement(Long pretId, String datePaiement, 
                                                     BigDecimal montant, Integer idMethodeRemboursement) {
        try {
            String url = serverUrl + "/" + pretId + "/payer";
            LOGGER.info("Appel POST vers: " + url);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("datePaiement", datePaiement);
            requestBody.put("montant", montant);
            requestBody.put("idMethodeRemboursement", idMethodeRemboursement);
            
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new org.springframework.http.HttpEntity<>(requestBody),
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors du paiement du remboursement: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurPret");
            
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "Erreur lors du traitement du paiement");
            return errorResult;
        }
    }
}