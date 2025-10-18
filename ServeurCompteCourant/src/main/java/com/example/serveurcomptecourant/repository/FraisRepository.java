package com.example.serveurcomptecourant.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

import com.example.serveurcomptecourant.models.Frais;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class FraisRepository {
    
    @PersistenceContext(unitName = "ServeurCompteCourantPU")
    private EntityManager em;

    public void save(Frais frais) {
        if (frais.getId() == null) {
            em.persist(frais);
        } else {
            em.merge(frais);
        }
    }

    public Frais find(Integer id) {
        try {
            return em.find(Frais.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Frais> findAll() {
        try {
            return em.createQuery("SELECT f FROM Frais f ORDER BY f.dateDebut DESC", Frais.class)
                    .getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Frais> findByNom(String nom) {
        try {
            return em.createQuery("SELECT f FROM Frais f WHERE f.nom = :nom ORDER BY f.dateDebut DESC", Frais.class)
                    .setParameter("nom", nom)
                    .getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public Frais findCurrentFrais(String nom, BigDecimal montant, LocalDateTime dateReference) {
        try {
            return em.createQuery("SELECT f FROM Frais f WHERE f.nom = :nom AND f.montantMin <= :montant AND f.montantMax >= :montant AND f.dateDebut <= :dateReference ORDER BY f.dateDebut DESC", Frais.class)
                    .setParameter("nom", nom)
                    .setParameter("montant", montant)
                    .setParameter("dateReference", dateReference)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public void delete(Integer id) {
        try {
            Frais frais = find(id);
            if (frais != null) {
                em.remove(frais);
            }
        } catch (Exception e) {
            // Log error
        }
    }
}