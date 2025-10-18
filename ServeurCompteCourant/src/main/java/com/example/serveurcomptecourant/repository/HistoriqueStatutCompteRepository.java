package com.example.serveurcomptecourant.repository;

import com.example.serveurcomptecourant.models.HistoriqueStatutCompte;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class HistoriqueStatutCompteRepository {

    @PersistenceContext(unitName = "ServeurCompteCourantPU")
    private EntityManager em;

    /**
     * Sauvegarde ou met à jour un historique de statut de compte
     */
    public HistoriqueStatutCompte save(HistoriqueStatutCompte historiqueStatutCompte) {
        if (historiqueStatutCompte.getId() == null) {
            em.persist(historiqueStatutCompte);
            return historiqueStatutCompte;
        } else {
            return em.merge(historiqueStatutCompte);
        }
    }

    /**
     * Trouve un historique de statut de compte par ID
     */
    public HistoriqueStatutCompte findById(Integer id) {
        return em.find(HistoriqueStatutCompte.class, id);
    }

    /**
     * Trouve tous les historiques de statut de compte
     */
    public List<HistoriqueStatutCompte> findAll() {
        TypedQuery<HistoriqueStatutCompte> query = em.createQuery(
            "SELECT h FROM HistoriqueStatutCompte h ORDER BY h.dateChangement DESC", 
            HistoriqueStatutCompte.class
        );
        return query.getResultList();
    }

    /**
     * Trouve tous les historiques de statut d'un compte
     */
    public List<HistoriqueStatutCompte> findByCompte(String idCompte) {
        TypedQuery<HistoriqueStatutCompte> query = em.createQuery(
            "SELECT h FROM HistoriqueStatutCompte h WHERE h.idCompte = :idCompte ORDER BY h.dateChangement DESC", 
            HistoriqueStatutCompte.class
        );
        query.setParameter("idCompte", idCompte);
        return query.getResultList();
    }

    /**
     * Trouve tous les historiques d'un type de statut
     */
    public List<HistoriqueStatutCompte> findByTypeStatut(Integer idTypeStatutCompte) {
        TypedQuery<HistoriqueStatutCompte> query = em.createQuery(
            "SELECT h FROM HistoriqueStatutCompte h WHERE h.idTypeStatutCompte = :idTypeStatutCompte ORDER BY h.dateChangement DESC", 
            HistoriqueStatutCompte.class
        );
        query.setParameter("idTypeStatutCompte", idTypeStatutCompte);
        return query.getResultList();
    }

    /**
     * Trouve le statut actuel d'un compte (le plus récent)
     */
    public HistoriqueStatutCompte findCurrentStatutByCompte(String idCompte) {
        TypedQuery<HistoriqueStatutCompte> query = em.createQuery(
            "SELECT h FROM HistoriqueStatutCompte h WHERE h.idCompte = :idCompte ORDER BY h.dateChangement DESC", 
            HistoriqueStatutCompte.class
        );
        query.setParameter("idCompte", idCompte);
        query.setMaxResults(1);
        List<HistoriqueStatutCompte> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}