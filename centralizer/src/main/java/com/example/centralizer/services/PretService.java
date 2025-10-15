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

import java.util.List;
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
    public List<Pret> getPretsByClientId(Long clientId) {
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

    /* ENDPOINTS NON IMPLEMENTES DANS LE SERVEUR - COMMENTES
    
    /**
     * Récupère un prêt par son ID - ENDPOINT NON DISPONIBLE
     */
    /*
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
    */

    /**
     * Crée un nouveau prêt
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

    /* ENDPOINTS NON IMPLEMENTES DANS LE SERVEUR - COMMENTES
    
    /**
     * Met à jour un prêt existant - ENDPOINT NON DISPONIBLE
     */
    /*
    public Pret updatePret(Long id, Pret pret) {
        try {
            String url = serverUrl + "/" + id;
            LOGGER.info("Appel PUT vers: " + url);
            
            ResponseEntity<Pret> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                null,
                Pret.class
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la mise à jour du prêt " + id + ": " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurPret");
            return null;
        }
    }

    /**
     * Effectue un remboursement sur un prêt - ENDPOINT NON DISPONIBLE
     */
    /*
    public Pret effectuerRemboursement(Long pretId, double montant) {
        try {
            String url = serverUrl + "/" + pretId + "/remboursement?montant=" + montant;
            LOGGER.info("Appel PUT vers: " + url);
            
            ResponseEntity<Pret> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                null,
                Pret.class
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors du remboursement du prêt " + pretId + ": " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurPret");
            return null;
        }
    }
    */
}