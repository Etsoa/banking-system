package com.example.centralizer.services;

import com.example.centralizer.models.compteDepotDTO.Compte;
import com.example.centralizer.models.compteDepotDTO.ClientRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
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
            LOGGER.info("=== DEBUG CompteDepotService ===");
            LOGGER.info("serverUrl configuré: " + serverUrl);
            LOGGER.info("URL finale utilisée: " + url);
            LOGGER.info("Appel GET vers: " + url);
            
            ResponseEntity<List<Compte>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Compte>>() {}
            );
            
            LOGGER.info("Réponse reçue avec succès, nombre de comptes: " + (response.getBody() != null ? response.getBody().size() : 0));
            return response.getBody();
        } catch (RestClientException e) {
            LOGGER.severe("=== ERREUR RestClient ===");
            LOGGER.severe("Type d'exception: " + e.getClass().getSimpleName());
            LOGGER.severe("Message d'erreur: " + e.getMessage());
            LOGGER.severe("Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "Aucune"));
            exceptionHandlingService.handleServerException(e, "ServeurCompteDepot");
            return null;
        }
    }

    /**
     * Récupère les comptes dépôt d'un client spécifique
     */
    public List<Compte> getComptesByClientId(int clientId) {
        try {
            String url = serverUrl + "/by-client";
            LOGGER.info("Appel POST vers: " + url);
            
            ClientRequest request = new ClientRequest(clientId);
            HttpEntity<ClientRequest> httpEntity = new HttpEntity<>(request);
            ResponseEntity<List<Compte>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
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
}