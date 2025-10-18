package com.example.serveurpret.repository;

import com.example.serveurpret.models.Frais;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class FraisRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Frais> findAll() {
        return em.createQuery("SELECT f FROM Frais f ORDER BY f.dateDebut DESC", Frais.class)
                .getResultList();
    }

    public Frais findById(Integer id) {
        return em.find(Frais.class, id);
    }

    public List<Frais> findByNom(String nom) {
        return em.createQuery("SELECT f FROM Frais f WHERE f.nom = :nom ORDER BY f.dateDebut DESC", Frais.class)
                .setParameter("nom", nom)
                .getResultList();
    }

    /**
     * Récupère le frais le plus récent par nom
     */
    public Frais findCurrentByNom(String nom) {
        List<Frais> frais = findByNom(nom);
        return frais.isEmpty() ? null : frais.get(0);
    }

    public Frais findCurrentFrais(String nom) {
        return em.createQuery("SELECT f FROM Frais f WHERE f.nom = :nom AND f.dateDebut <= :today ORDER BY f.dateDebut DESC", Frais.class)
                .setParameter("nom", nom)
                .setParameter("today", LocalDateTime.now())
                .setMaxResults(1)
                .getSingleResult();
    }

    public Frais findFraisAtDate(String nom, LocalDateTime date) {
        return em.createQuery("SELECT f FROM Frais f WHERE f.nom = :nom AND f.dateDebut <= :date ORDER BY f.dateDebut DESC", Frais.class)
                .setParameter("nom", nom)
                .setParameter("date", date)
                .setMaxResults(1)
                .getSingleResult();
    }

    public List<Frais> findByDateRange(LocalDateTime dateDebut, LocalDateTime dateFin) {
        return em.createQuery("SELECT f FROM Frais f WHERE f.dateDebut >= :dateDebut AND f.dateDebut <= :dateFin ORDER BY f.dateDebut", Frais.class)
                .setParameter("dateDebut", dateDebut)
                .setParameter("dateFin", dateFin)
                .getResultList();
    }

    public Frais save(Frais frais) {
        if (frais.getId() == null) {
            em.persist(frais);
            return frais;
        } else {
            return em.merge(frais);
        }
    }

    public void delete(Frais frais) {
        em.remove(em.contains(frais) ? frais : em.merge(frais));
    }
}