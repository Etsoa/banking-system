
package com.example.centralizer.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import java.util.List;
import java.util.Map;

@Service
public class CompteDepotProxyService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:5086/api/CompteDepot";

    public List<?> getAllComptesDepot() {
        return restTemplate.getForObject(baseUrl, List.class);
    }

    public Object getCompteDepotById(int id) {
        String url = baseUrl + "/" + id;
        return restTemplate.getForObject(url, Object.class);
    }

    public Object createCompteDepot(Map<String, Object> compteDepot) {
        return restTemplate.postForObject(baseUrl, compteDepot, Object.class);
    }

    public Object updateCompteDepot(int id, Map<String, Object> compteDepot) {
        String url = baseUrl + "/" + id;
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(compteDepot);
        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Object.class);
        return response.getBody();
    }

    public void deleteCompteDepot(int id) {
        String url = baseUrl + "/" + id;
        restTemplate.delete(url);
    }
}
