package com.example.serveurpret.repository;

import com.example.serveurpret.models.Remboursement;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class RemboursementRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Remboursement> findAll() {
        return em.createQuery("SELECT r FROM Remboursement r", Remboursement.class)
                .getResultList();
    }

    public List<Remboursement> findByPretId(Integer pretId) {
        return em.createQuery("SELECT r FROM Remboursement r WHERE r.idPret = :pretId", Remboursement.class)
                .setParameter("pretId", pretId)
                .getResultList();
    }

    public Remboursement findById(Integer id) {
        return em.find(Remboursement.class, id);
    }

    public Remboursement save(Remboursement remboursement) {
        if (remboursement.getId() == null) {
            em.persist(remboursement);
            return remboursement;
        } else {
            return em.merge(remboursement);
        }
    }

    public void delete(Integer id) {
        Remboursement remboursement = findById(id);
        if (remboursement != null) {
            em.remove(remboursement);
        }
    }

    /**
     * Récupère les remboursements payés d'un prêt
     */
    public List<Remboursement> findByPretIdAndPaid(Integer pretId) {
        return em.createQuery("SELECT r FROM Remboursement r WHERE r.idPret = :pretId AND r.datePaiement IS NOT NULL", Remboursement.class)
                .setParameter("pretId", pretId)
                .getResultList();
    }

    /**
     * Crée un nouveau remboursement
     */
    public void create(Remboursement remboursement) {
        em.persist(remboursement);
    }
}