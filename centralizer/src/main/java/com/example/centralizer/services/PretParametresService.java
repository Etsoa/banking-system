package com.example.centralizer.services;

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
import java.util.Map;
import java.util.logging.Logger;

@Service
public class PretParametresService {
    private static final Logger LOGGER = Logger.getLogger(PretParametresService.class.getName());

    @Value("${serveur.pret.url}")
    private String serverUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    /**
     * Récupère toutes les modalités disponibles
     */
    public List<Map<String, Object>> getAllModalites() {
        try {
            String url = serverUrl + "/parametres/modalites";
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération des modalités: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurPret");
            return null;
        }
    }

    /**
     * Récupère tous les types de remboursement disponibles
     */
    public List<Map<String, Object>> getAllTypesRemboursement() {
        try {
            String url = serverUrl + "/parametres/types-remboursement";
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération des types de remboursement: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurPret");
            return null;
        }
    }

    /**
     * Récupère la plage de durée selon le montant
     */
    public Map<String, Object> getPlageDureeByMontant(BigDecimal montant) {
        try {
            String url = serverUrl + "/parametres/plage-duree?montant=" + montant;
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération de la plage de durée: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurPret");
            return null;
        }
    }

    /**
     * Récupère l'ID du statut "En cours"
     */
    public Integer getStatutEnCoursId() {
        try {
            String url = serverUrl + "/parametres/statut-en-cours";
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<Integer> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Integer>() {}
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("Erreur lors de la récupération du statut En cours: " + e.getMessage());
            exceptionHandlingService.handleServerException(e, "ServeurPret");
            return 1; // Valeur par défaut
        }
    }
}