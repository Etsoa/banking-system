
package com.example.centralizer.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import com.example.centralizer.models.compteDepotDTO.Compte;
import com.example.centralizer.models.compteDepotDTO.Transaction;
import com.example.centralizer.models.compteDepotDTO.Transfert;
import java.util.List;
import java.util.Arrays;

@Service
public class CompteDepotProxyService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:5086/api/CompteDepot";

    public List<Compte> getAllComptesDepot() {
        ResponseEntity<Compte[]> response = restTemplate.getForEntity(baseUrl, Compte[].class);
        return Arrays.asList(response.getBody());
    }

    public Compte getCompteDepotById(int id) {
        String url = baseUrl + "/" + id;
        return restTemplate.getForObject(url, Compte.class);
    }

    public Compte createCompteDepot(Compte compte) {
        return restTemplate.postForObject(baseUrl, compte, Compte.class);
    }

    public Compte updateCompteDepot(int id, Compte compte) {
        String url = baseUrl + "/" + id;
        HttpEntity<Compte> entity = new HttpEntity<>(compte);
        ResponseEntity<Compte> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Compte.class);
        return response.getBody();
    }

    public void deleteCompteDepot(int id) {
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
