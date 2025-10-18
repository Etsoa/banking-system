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
}