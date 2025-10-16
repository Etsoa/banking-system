package com.example.serveurcomptecourant.repository;

import com.example.serveurcomptecourant.models.HistoriqueSolde;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class HistoriqueSoldeRepository {

    @PersistenceContext(unitName = "ServeurCompteCourantPU")
    private EntityManager em;

    /**
     * Sauvegarde ou met à jour un historique de solde
     */
    public HistoriqueSolde save(HistoriqueSolde historiqueSolde) {
        if (historiqueSolde.getId() == null) {
            em.persist(historiqueSolde);
            return historiqueSolde;
        } else {
            return em.merge(historiqueSolde);
        }
    }

    /**
     * Trouve un historique de solde par ID
     */
    public HistoriqueSolde findById(Integer id) {
        return em.find(HistoriqueSolde.class, id);
    }

    /**
     * Trouve tous les historiques de solde
     */
    public List<HistoriqueSolde> findAll() {
        TypedQuery<HistoriqueSolde> query = em.createQuery(
            "SELECT h FROM HistoriqueSolde h ORDER BY h.dateChangement DESC", 
            HistoriqueSolde.class
        );
        return query.getResultList();
    }

    /**
     * Trouve tous les historiques de solde d'un compte
     */
    public List<HistoriqueSolde> findByCompte(String idCompte) {
        TypedQuery<HistoriqueSolde> query = em.createQuery(
            "SELECT h FROM HistoriqueSolde h WHERE h.idCompte = :idCompte ORDER BY h.dateChangement DESC", 
            HistoriqueSolde.class
        );
        query.setParameter("idCompte", idCompte);
        return query.getResultList();
    }

    /**
     * Trouve tous les historiques de solde d'une transaction
     */
    public List<HistoriqueSolde> findByTransaction(Integer idTransaction) {
        TypedQuery<HistoriqueSolde> query = em.createQuery(
            "SELECT h FROM HistoriqueSolde h WHERE h.idTransaction = :idTransaction ORDER BY h.dateChangement DESC", 
            HistoriqueSolde.class
        );
        query.setParameter("idTransaction", idTransaction);
        return query.getResultList();
    }

    /**
     * Trouve les historiques de solde par période
     */
    public List<HistoriqueSolde> findByDateRange(LocalDateTime dateDebut, LocalDateTime dateFin) {
        TypedQuery<HistoriqueSolde> query = em.createQuery(
            "SELECT h FROM HistoriqueSolde h WHERE h.dateChangement BETWEEN :dateDebut AND :dateFin ORDER BY h.dateChangement DESC", 
            HistoriqueSolde.class
        );
        query.setParameter("dateDebut", dateDebut);
        query.setParameter("dateFin", dateFin);
        return query.getResultList();
    }

    /**
     * Trouve le dernier historique de solde d'un compte
     */
    public HistoriqueSolde findLastByCompte(String idCompte) {
        TypedQuery<HistoriqueSolde> query = em.createQuery(
            "SELECT h FROM HistoriqueSolde h WHERE h.idCompte = :idCompte ORDER BY h.dateChangement DESC", 
            HistoriqueSolde.class
        );
        query.setParameter("idCompte", idCompte);
        query.setMaxResults(1);
        List<HistoriqueSolde> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Supprime un historique de solde par ID
     */
    public boolean deleteById(Integer id) {
        HistoriqueSolde historiqueSolde = findById(id);
        if (historiqueSolde != null) {
            em.remove(historiqueSolde);
            return true;
        }
        return false;
    }

    /**
     * Compte le nombre total d'historiques de solde
     */
    public long count() {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(h) FROM HistoriqueSolde h", 
            Long.class
        );
        return query.getSingleResult();
    }

    /**
     * Compte le nombre d'historiques de solde pour un compte
     */
    public long countByCompte(String idCompte) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(h) FROM HistoriqueSolde h WHERE h.idCompte = :idCompte", 
            Long.class
        );
        query.setParameter("idCompte", idCompte);
        return query.getSingleResult();
    }
}