
package com.example.centralizer.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import java.util.List;
import java.util.Map;

@Service
public class CompteCourantProxyService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8080/comptecourant/api/compte-courant";

    public List<?> getAllComptesCourant() {
        return restTemplate.getForObject(baseUrl, List.class);
    }

    public Object getCompteCourantById(int id) {
        String url = baseUrl + "/" + id;
        return restTemplate.getForObject(url, Object.class);
    }

    public Object createCompteCourant(Map<String, Object> compteCourant) {
        return restTemplate.postForObject(baseUrl, compteCourant, Object.class);
    }

    public Object updateCompteCourant(int id, Map<String, Object> compteCourant) {
        String url = baseUrl + "/" + id;
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(compteCourant);
        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Object.class);
        return response.getBody();
    }

    public void deleteCompteCourant(int id) {
        String url = baseUrl + "/" + id;
        restTemplate.delete(url);
    }
}
