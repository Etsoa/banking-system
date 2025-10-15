package com.example.centralizer.services;

import com.example.centralizer.models.compteCourantDTO.CompteCourant;
import com.example.centralizer.models.compteCourantDTO.CompteCourantAvecStatut;
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
 * Service pour communiquer avec le ServeurCompteCourant
 * Gère automatiquement les exceptions provenant du serveur
 */
@Service
public class CompteCourantService {
    private static final Logger LOGGER = Logger.getLogger(CompteCourantService.class.getName());

    @Value("${serveur.comptecourant.url}")
    private String serverUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    /**
     * Récupère tous les comptes courants
     */
    public List<CompteCourant> getAllComptes() {
        try {
            String url = serverUrl;
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<List<CompteCourant>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CompteCourant>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération des comptes: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteCourant");
            return null; // Ne sera jamais atteint car handleServerException lance une exception
        }
    }

    /**
     * Récupère les comptes d'un client spécifique
     */
    public List<CompteCourant> getComptesByClientId(int clientId) {
        try {
            String url = serverUrl + "/client/" + clientId;
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<List<CompteCourant>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CompteCourant>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération des comptes du client " + clientId + ": " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteCourant");
            return null;
        }
    }

    /**
     * Récupère un compte par son ID
     */
    public CompteCourant getCompteById(int id) {
        try {
            String url = serverUrl + "/" + id;
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<CompteCourant> response = restTemplate.getForEntity(url, CompteCourant.class);
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération du compte " + id + ": " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteCourant");
            return null;
        }
    }

    /**
     * Crée un nouveau compte courant
     */
    public CompteCourant createCompte(CompteCourant compte) {
        try {
            String url = serverUrl;
            LOGGER.info("Appel POST vers: " + url);
            
            ResponseEntity<CompteCourant> response = restTemplate.postForEntity(url, compte, CompteCourant.class);
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la creation de compte: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteCourant");
            return null;
        }
    }

    /**
     * Récupère tous les comptes courants avec leur statut
     */
    public List<CompteCourantAvecStatut> getAllComptesAvecStatut() {
        try {
            String url = serverUrl + "/avec-statut";
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<List<CompteCourantAvecStatut>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CompteCourantAvecStatut>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération des comptes avec statut: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteCourant");
            return null;
        }
    }

    /* ENDPOINTS NON IMPLEMENTES DANS LE SERVEUR - COMMENTES
    
    /**
     * Débite un compte - ENDPOINT NON DISPONIBLE
     */
    /*
    public CompteCourant debiterCompte(Long compteId, double montant) {
        try {
            String url = serverUrl + "/" + compteId + "/debit?montant=" + montant;
            LOGGER.info("Appel PUT vers: " + url);
            
            ResponseEntity<CompteCourant> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                null,
                CompteCourant.class
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors du débit du compte " + compteId + ": " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteCourant");
            return null;
        }
    }

    /**
     * Crédite un compte - ENDPOINT NON DISPONIBLE
     */
    /*
    public CompteCourant crediterCompte(Long compteId, double montant) {
        try {
            String url = serverUrl + "/" + compteId + "/credit?montant=" + montant;
            LOGGER.info("Appel PUT vers: " + url);
            
            ResponseEntity<CompteCourant> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                null,
                CompteCourant.class
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors du crédit du compte " + compteId + ": " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurCompteCourant");
            return null;
        }
    }

    /**
     * Test de communication avec le serveur pour générer une exception - ENDPOINT NON DISPONIBLE
     */
    /*
    public void testException() {
        try {
            String url = serverUrl + "/test-exception";
            LOGGER.info("Test d'exception vers: " + url);
            
            restTemplate.getForEntity(url, String.class);
        } catch (RestClientException e) {
            LOGGER.info("Exception de test reçue comme attendu");
            exceptionHandlingService.handleServerException(e, "ServeurCompteCourant");
        }
    }
    */
}