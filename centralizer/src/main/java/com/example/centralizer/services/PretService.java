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
    public List<Pret> getPretsByClientId(String clientId) {
        try {
            String url = serverUrl + "/pret/client/" + clientId;
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
}