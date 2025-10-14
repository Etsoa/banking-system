
package com.example.centralizer.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import com.example.centralizer.models.compteCourantDTO.CompteCourant;
import java.util.List;
import java.util.Arrays;

@Service
public class CompteCourantProxyService {
    private final RestTemplate restTemplate = new RestTemplate();
    // URL pour WildFly - Le service doit être déployé sur WildFly comme comptecourant.war
    private final String baseUrl = "http://localhost:8080/comptecourant/api/compte-courant";

    public List<CompteCourant> getAllComptesCourant() {
        try {
            ResponseEntity<CompteCourant[]> response = restTemplate.getForEntity(baseUrl, CompteCourant[].class);
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            // Service non disponible - retourner une liste vide avec un message de log
            System.err.println("ServeurCompteCourant non disponible: " + e.getMessage());
            return Arrays.asList(); // Liste vide
        }
    }

    public CompteCourant getCompteCourantById(int id) {
        try {
            String url = baseUrl + "/" + id;
            return restTemplate.getForObject(url, CompteCourant.class);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du compte courant " + id + ": " + e.getMessage());
            return null;
        }
    }

    public List<CompteCourant> getComptesCourantByClientId(int clientId) {
        try {
            String url = baseUrl + "/client/" + clientId;
            ResponseEntity<CompteCourant[]> response = restTemplate.getForEntity(url, CompteCourant[].class);
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des comptes courants pour le client " + clientId + ": " + e.getMessage());
            return Arrays.asList();
        }
    }

    public CompteCourant createCompteCourant(CompteCourant compteCourant) {
        try {
            return restTemplate.postForObject(baseUrl, compteCourant, CompteCourant.class);
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du compte courant: " + e.getMessage());
            return null;
        }
    }

    public CompteCourant updateCompteCourant(int id, CompteCourant compteCourant) {
        try {
            String url = baseUrl + "/" + id;
            HttpEntity<CompteCourant> entity = new HttpEntity<>(compteCourant);
            ResponseEntity<CompteCourant> response = restTemplate.exchange(url, HttpMethod.PUT, entity, CompteCourant.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du compte courant " + id + ": " + e.getMessage());
            return null;
        }
    }

    public void deleteCompteCourant(int id) {
        try {
            String url = baseUrl + "/" + id;
            restTemplate.delete(url);
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression du compte courant " + id + ": " + e.getMessage());
        }
    }

    
}
