package com.example.serveurpret.repository;

import com.example.serveurpret.models.StatutRemboursement;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class StatutRemboursementRepository {

    @PersistenceContext
    private EntityManager em;

    public List<StatutRemboursement> findAll() {
        return em.createQuery("SELECT sr FROM StatutRemboursement sr", StatutRemboursement.class)
                .getResultList();
    }

    public List<StatutRemboursement> findActives() {
        return em.createQuery("SELECT sr FROM StatutRemboursement sr WHERE sr.actif = true", StatutRemboursement.class)
                .getResultList();
    }

    public StatutRemboursement findById(Integer id) {
        return em.find(StatutRemboursement.class, id);
    }

    public StatutRemboursement findByLibelle(String libelle) {
        return em.createQuery("SELECT sr FROM StatutRemboursement sr WHERE sr.libelle = :libelle", StatutRemboursement.class)
                .setParameter("libelle", libelle)
                .getSingleResult();
    }

    public List<StatutRemboursement> findAllOrderByLibelle() {
        return em.createQuery("SELECT sr FROM StatutRemboursement sr ORDER BY sr.libelle ASC", StatutRemboursement.class)
                .getResultList();
    }

    public StatutRemboursement save(StatutRemboursement statutRemboursement) {
        if (statutRemboursement.getId() == null) {
            em.persist(statutRemboursement);
            return statutRemboursement;
        } else {
            return em.merge(statutRemboursement);
        }
    }

    public void delete(StatutRemboursement statutRemboursement) {
        em.remove(em.contains(statutRemboursement) ? statutRemboursement : em.merge(statutRemboursement));
    }
}