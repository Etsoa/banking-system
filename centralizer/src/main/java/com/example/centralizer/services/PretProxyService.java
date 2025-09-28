
package com.example.centralizer.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import java.util.List;
import java.util.Map;

@Service
public class PretProxyService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8080/pret/api/pret";

    public List<?> getAllPrets() {
        return restTemplate.getForObject(baseUrl, List.class);
    }

    public Object getPretById(int id) {
        String url = baseUrl + "/" + id;
        return restTemplate.getForObject(url, Object.class);
    }

    public Object createPret(Map<String, Object> pret) {
        return restTemplate.postForObject(baseUrl, pret, Object.class);
    }

    public Object updatePret(int id, Map<String, Object> pret) {
        String url = baseUrl + "/" + id;
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(pret);
        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Object.class);
        return response.getBody();
    }

    public void deletePret(int id) {
        String url = baseUrl + "/" + id;
        restTemplate.delete(url);
    }
}
