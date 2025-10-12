package com.example.serveurcomptecourant.services;

import com.example.serveurcomptecourant.models.CompteCourant;
import com.example.serveurcomptecourant.repository.CompteCourantRepository;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class CompteCourantService {
    @PersistenceContext(unitName = "ServeurCompteCourantPU")
    private EntityManager em;
    
    @EJB
    private CompteCourantRepository repository;

    public List<CompteCourant> getAllComptes() {
        return repository.findAll();
    }

    public List<CompteCourant> getComptesByClientId(Long clientId) {
        return repository.findByClientId(clientId);
    }

    public CompteCourant getCompteById(Long id) {
        return repository.find(id);
    }

    public void createCompte(CompteCourant compte) {
        repository.save(compte);
    }
}
