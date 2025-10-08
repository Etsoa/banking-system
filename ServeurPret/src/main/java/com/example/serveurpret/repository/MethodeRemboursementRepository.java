package com.example.serveurpret.repository;

import com.example.serveurpret.models.MethodeRemboursement;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class MethodeRemboursementRepository {

    @PersistenceContext(unitName = "ServeurPretPU")
    private EntityManager em;

    /**
     * Sauvegarde ou met à jour une méthode de remboursement
     */
    public MethodeRemboursement save(MethodeRemboursement methodeRemboursement) {
        if (methodeRemboursement.getId() == null) {
            em.persist(methodeRemboursement);
            return methodeRemboursement;
        } else {
            return em.merge(methodeRemboursement);
        }
    }

    /**
     * Trouve une méthode de remboursement par ID
     */
    public MethodeRemboursement findById(Integer id) {
        return em.find(MethodeRemboursement.class, id);
    }

    /**
     * Trouve toutes les méthodes de remboursement
     */
    public List<MethodeRemboursement> findAll() {
        TypedQuery<MethodeRemboursement> query = em.createQuery(
            "SELECT mr FROM MethodeRemboursement mr ORDER BY mr.libelle", 
            MethodeRemboursement.class
        );
        return query.getResultList();
    }

    /**
     * Trouve toutes les méthodes de remboursement actives
     */
    public List<MethodeRemboursement> findAllActives() {
        TypedQuery<MethodeRemboursement> query = em.createQuery(
            "SELECT mr FROM MethodeRemboursement mr WHERE mr.actif = true ORDER BY mr.libelle", 
            MethodeRemboursement.class
        );
        return query.getResultList();
    }

    /**
     * Trouve une méthode de remboursement par libellé
     */
    public MethodeRemboursement findByLibelle(String libelle) {
        TypedQuery<MethodeRemboursement> query = em.createQuery(
            "SELECT mr FROM MethodeRemboursement mr WHERE mr.libelle = :libelle", 
            MethodeRemboursement.class
        );
        query.setParameter("libelle", libelle);
        List<MethodeRemboursement> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Supprime une méthode de remboursement par ID
     */
    public boolean deleteById(Integer id) {
        MethodeRemboursement methodeRemboursement = findById(id);
        if (methodeRemboursement != null) {
            em.remove(methodeRemboursement);
            return true;
        }
        return false;
    }

    /**
     * Vérifie si une méthode de remboursement existe par ID
     */
    public boolean existsById(Integer id) {
        return findById(id) != null;
    }

    /**
     * Compte le nombre total de méthodes de remboursement
     */
    public long count() {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(mr) FROM MethodeRemboursement mr", 
            Long.class
        );
        return query.getSingleResult();
    }

    /**
     * Active ou désactive une méthode de remboursement
     */
    public MethodeRemboursement toggleActif(Integer id) {
        MethodeRemboursement methodeRemboursement = findById(id);
        if (methodeRemboursement != null) {
            methodeRemboursement.setActif(!methodeRemboursement.getActif());
            return em.merge(methodeRemboursement);
        }
        return null;
    }
}
