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
        if (compte.getId() == null) {
            em.persist(compte);
        } else {
            em.merge(compte);
        }
    }

    public CompteCourant find(Long id) {
        return em.find(CompteCourant.class, id);
    }

    public List<CompteCourant> findAll() {
        return em.createQuery("SELECT c FROM CompteCourant c", CompteCourant.class).getResultList();
    }

    public List<CompteCourant> findByClientId(Long clientId) {
        return em.createQuery("SELECT c FROM CompteCourant c WHERE c.idClient = :clientId", CompteCourant.class)
                .setParameter("clientId", clientId)
                .getResultList();
    }

    public void delete(CompteCourant compte) {
        em.remove(em.contains(compte) ? compte : em.merge(compte));
    }
}
