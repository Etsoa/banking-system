
package com.example.centralizer.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import com.example.centralizer.models.compteCourantDTO.CompteCourant;
import com.example.centralizer.models.compteCourantDTO.Transaction;
import com.example.centralizer.models.compteCourantDTO.Transfert;
import java.util.List;
import java.util.Arrays;

@Service
public class CompteCourantProxyService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8080/comptecourant/api/compte-courant";

    public List<CompteCourant> getAllComptesCourant() {
        ResponseEntity<CompteCourant[]> response = restTemplate.getForEntity(baseUrl, CompteCourant[].class);
        return Arrays.asList(response.getBody());
    }

    public CompteCourant getCompteCourantById(int id) {
        String url = baseUrl + "/" + id;
        return restTemplate.getForObject(url, CompteCourant.class);
    }

    public CompteCourant createCompteCourant(CompteCourant compteCourant) {
        return restTemplate.postForObject(baseUrl, compteCourant, CompteCourant.class);
    }

    public CompteCourant updateCompteCourant(int id, CompteCourant compteCourant) {
        String url = baseUrl + "/" + id;
        HttpEntity<CompteCourant> entity = new HttpEntity<>(compteCourant);
        ResponseEntity<CompteCourant> response = restTemplate.exchange(url, HttpMethod.PUT, entity, CompteCourant.class);
        return response.getBody();
    }

    public void deleteCompteCourant(int id) {
        String url = baseUrl + "/" + id;
        restTemplate.delete(url);
    }

    // Méthodes pour les transactions
    public List<Transaction> getTransactionsByCompteId(int compteId) {
        String url = baseUrl + "/" + compteId + "/transactions";
        ResponseEntity<Transaction[]> response = restTemplate.getForEntity(url, Transaction[].class);
        return Arrays.asList(response.getBody());
    }

    public Transaction createTransaction(int compteId, Transaction transaction) {
        String url = baseUrl + "/" + compteId + "/transactions";
        return restTemplate.postForObject(url, transaction, Transaction.class);
    }

    // Méthodes pour les transferts
    public List<Transfert> getTransfertsByCompteId(int compteId) {
        String url = baseUrl + "/" + compteId + "/transferts";
        ResponseEntity<Transfert[]> response = restTemplate.getForEntity(url, Transfert[].class);
        return Arrays.asList(response.getBody());
    }

    public Transfert createTransfert(Transfert transfert) {
        String url = baseUrl + "/transferts";
        return restTemplate.postForObject(url, transfert, Transfert.class);
    }
}
