package com.example.serveurcomptecourant.repository;

import java.util.List;

import com.example.serveurcomptecourant.models.CompteCourant;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class CompteCourantRepository {
    @PersistenceContext(unitName = "ServeurCompteCourantPU")
    private EntityManager em;

    public void save(CompteCourant compte) {
        if (compte.getIdCompte() == null) {
            em.persist(compte);
        } else {
            em.merge(compte);
        }
    }

    public CompteCourant find(Integer idCompte) {
        return em.find(CompteCourant.class, idCompte);
    }

    public List<CompteCourant> findAll() {
        return em.createQuery("SELECT c FROM CompteCourant c", CompteCourant.class).getResultList();
    }

    public void delete(CompteCourant compte) {
        em.remove(em.contains(compte) ? compte : em.merge(compte));
    }
}
