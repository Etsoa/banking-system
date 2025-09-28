package com.example.serveurcomptecourant.repository;

import com.example.serveurcomptecourant.models.CompteCourant;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class CompteCourantRepository {
    @PersistenceContext(unitName = "ServeurCompteCourantPU")
    private EntityManager em;

    public void save(CompteCourant compte) {
        em.persist(compte);
    }

    public CompteCourant find(Long id) {
        return em.find(CompteCourant.class, id);
    }

    public List<CompteCourant> findAll() {
        return em.createQuery("SELECT c FROM CompteCourant c", CompteCourant.class).getResultList();
    }
}
