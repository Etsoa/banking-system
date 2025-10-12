
package com.example.centralizer.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import com.example.centralizer.models.pretDTO.Pret;
import com.example.centralizer.models.pretDTO.Remboursement;
import java.util.List;
import java.util.Arrays;

@Service
public class PretProxyService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8080/pret/api/pret";

    public List<Pret> getAllPrets() {
        ResponseEntity<Pret[]> response = restTemplate.getForEntity(baseUrl, Pret[].class);
        return Arrays.asList(response.getBody());
    }

    public Pret getPretById(int id) {
        String url = baseUrl + "/" + id;
        return restTemplate.getForObject(url, Pret.class);
    }

    public List<Pret> getPretsByClientId(int clientId) {
        try {
            String url = baseUrl + "/client/" + clientId;
            ResponseEntity<Pret[]> response = restTemplate.getForEntity(url, Pret[].class);
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des prêts pour le client " + clientId + ": " + e.getMessage());
            return Arrays.asList();
        }
    }

    public Pret createPret(Pret pret) {
        return restTemplate.postForObject(baseUrl, pret, Pret.class);
    }

    public Pret updatePret(int id, Pret pret) {
        String url = baseUrl + "/" + id;
        HttpEntity<Pret> entity = new HttpEntity<>(pret);
        ResponseEntity<Pret> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Pret.class);
        return response.getBody();
    }

    public void deletePret(int id) {
        String url = baseUrl + "/" + id;
        restTemplate.delete(url);
    }

    // Méthodes pour les remboursements
    public List<Remboursement> getRemboursementsByPretId(int pretId) {
        String url = baseUrl + "/" + pretId + "/remboursements";
        ResponseEntity<Remboursement[]> response = restTemplate.getForEntity(url, Remboursement[].class);
        return Arrays.asList(response.getBody());
    }

    public Remboursement createRemboursement(int pretId, Remboursement remboursement) {
        String url = baseUrl + "/" + pretId + "/remboursements";
        return restTemplate.postForObject(url, remboursement, Remboursement.class);
    }
}
