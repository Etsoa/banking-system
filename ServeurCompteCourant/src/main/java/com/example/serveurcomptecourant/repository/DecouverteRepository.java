package com.example.serveurcomptecourant.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

import com.example.serveurcomptecourant.models.Decouverte;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class DecouverteRepository {
    
    @PersistenceContext(unitName = "ServeurCompteCourantPU")
    private EntityManager em;

    public void save(Decouverte decouverte) {
        if (decouverte.getId() == null) {
            em.persist(decouverte);
        } else {
            em.merge(decouverte);
        }
    }

    public Decouverte find(Integer id) {
        try {
            return em.find(Decouverte.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Decouverte> findAll() {
        try {
            return em.createQuery("SELECT d FROM Decouverte d ORDER BY d.dateDebut DESC", Decouverte.class)
                    .getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public Decouverte findCurrentDecouverte(BigDecimal revenu, LocalDateTime dateReference) {
        try {
            return em.createQuery("SELECT d FROM Decouverte d WHERE d.revenuMin <= :revenu AND d.revenuMax >= :revenu AND d.dateDebut <= :dateReference ORDER BY d.dateDebut DESC", Decouverte.class)
                    .setParameter("revenu", revenu)
                    .setParameter("dateReference", dateReference)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Decouverte> findByRevenuRange(BigDecimal revenuMin, BigDecimal revenuMax) {
        try {
            return em.createQuery("SELECT d FROM Decouverte d WHERE d.revenuMax >= :revenuMin AND d.revenuMin <= :revenuMax ORDER BY d.dateDebut DESC", Decouverte.class)
                    .setParameter("revenuMin", revenuMin)
                    .setParameter("revenuMax", revenuMax)
                    .getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public void delete(Integer id) {
        try {
            Decouverte decouverte = find(id);
            if (decouverte != null) {
                em.remove(decouverte);
            }
        } catch (Exception e) {
            // Log error
        }
    }
}