package com.example.centralizer.services;

import com.example.centralizer.models.compteDepotDTO.Compte;
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
 * Service pour communiquer avec le ServeurCompteDepot (.NET)
 * Gère automatiquement les exceptions provenant du serveur
 */
@Service
public class CompteDepotService {
    private static final Logger LOGGER = Logger.getLogger(CompteDepotService.class.getName());

    @Value("${serveur.compteDepot.url}")
    private String serverUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    /**
     * Récupère tous les comptes dépôt
     */
    public List<Compte> getAllComptes() {
        try {
            String url = serverUrl;
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<List<Compte>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Compte>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération des comptes dépôt: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteDepot");
            return null;
        }
    }

    /**
     * Récupère les comptes dépôt d'un client spécifique
     */
    public List<Compte> getComptesByClientId(int clientId) {
        try {
            String url = serverUrl + "/client/" + clientId;
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<List<Compte>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Compte>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération des comptes dépôt du client " + clientId + ": " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteDepot");
            return null;
        }
    }

    /**
     * Récupère un compte dépôt par son ID
     */
    public Compte getCompteById(String id) {
        try {
            String url = serverUrl + "/" + id;
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<Compte> response = restTemplate.getForEntity(url, Compte.class);
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération du compte dépôt " + id + ": " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteDepot");
            return null;
        }
    }

    /**
     * Crée un nouveau compte dépôt
     */
    public Compte createCompte(Compte compte) {
        try {
            String url = serverUrl;
            LOGGER.info("Appel POST vers: " + url);
            
            ResponseEntity<Compte> response = restTemplate.postForEntity(url, compte, Compte.class);
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la création du compte dépôt: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteDepot");
            return null;
        }
    }

    /* ENDPOINTS NON IMPLEMENTES DANS LE SERVEUR - COMMENTES
    
    /**
     * Met à jour un compte dépôt existant - ENDPOINT NON DISPONIBLE
     */
    /*
    public Compte updateCompte(Long id, Compte compte) {
        try {
            String url = serverUrl + "/" + id;
            LOGGER.info("Appel PUT vers: " + url);
            
            ResponseEntity<Compte> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                null,
                Compte.class
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la mise à jour du compte dépôt " + id + ": " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteDepot");
            return null;
        }
    }

    /**
     * Effectue un dépôt sur un compte - ENDPOINT NON DISPONIBLE
     */
    /*
    public Compte effectuerDepot(Long compteId, double montant) {
        try {
            String url = serverUrl + "/" + compteId + "/depot?montant=" + montant;
            LOGGER.info("Appel PUT vers: " + url);
            
            ResponseEntity<Compte> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                null,
                Compte.class
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors du dépôt sur le compte " + compteId + ": " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteDepot");
            return null;
        }
    }

    /**
     * Effectue un retrait sur un compte - ENDPOINT NON DISPONIBLE
     */
    /*
    public Compte effectuerRetrait(Long compteId, double montant) {
        try {
            String url = serverUrl + "/" + compteId + "/retrait?montant=" + montant;
            LOGGER.info("Appel PUT vers: " + url);
            
            ResponseEntity<Compte> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                null,
                Compte.class
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors du retrait sur le compte " + compteId + ": " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteDepot");
            return null;
        }
    }

    /**
     * Test de communication avec le serveur - ENDPOINT NON DISPONIBLE
     */
    /*
    public void testException() {
        try {
            String url = serverUrl + "/test-exception";
            LOGGER.info("Test d'exception vers: " + url);
            
            restTemplate.getForEntity(url, String.class);
        } catch (RestClientException e) {
            LOGGER.info("Exception de test reçue comme attendu");
            exceptionHandlingService.handleServerException(e, "ServeurCompteDepot");
        }
    }
    */
}