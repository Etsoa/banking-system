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
    public List<HistoriqueStatutCompte> findByCompte(Integer idCompte) {
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
     * Trouve les historiques de statut par période
     */
    public List<HistoriqueStatutCompte> findByDateRange(LocalDateTime dateDebut, LocalDateTime dateFin) {
        TypedQuery<HistoriqueStatutCompte> query = em.createQuery(
            "SELECT h FROM HistoriqueStatutCompte h WHERE h.dateChangement BETWEEN :dateDebut AND :dateFin ORDER BY h.dateChangement DESC", 
            HistoriqueStatutCompte.class
        );
        query.setParameter("dateDebut", dateDebut);
        query.setParameter("dateFin", dateFin);
        return query.getResultList();
    }

    /**
     * Trouve le statut actuel d'un compte (le plus récent)
     */
    public HistoriqueStatutCompte findCurrentStatutByCompte(Integer idCompte) {
        TypedQuery<HistoriqueStatutCompte> query = em.createQuery(
            "SELECT h FROM HistoriqueStatutCompte h WHERE h.idCompte = :idCompte ORDER BY h.dateChangement DESC", 
            HistoriqueStatutCompte.class
        );
        query.setParameter("idCompte", idCompte);
        query.setMaxResults(1);
        List<HistoriqueStatutCompte> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Trouve l'historique complet d'un compte (chronologique)
     */
    public List<HistoriqueStatutCompte> findHistoriqueCompletByCompte(Integer idCompte) {
        TypedQuery<HistoriqueStatutCompte> query = em.createQuery(
            "SELECT h FROM HistoriqueStatutCompte h WHERE h.idCompte = :idCompte ORDER BY h.dateChangement ASC", 
            HistoriqueStatutCompte.class
        );
        query.setParameter("idCompte", idCompte);
        return query.getResultList();
    }

    /**
     * Supprime un historique de statut de compte par ID
     */
    public boolean deleteById(Integer id) {
        HistoriqueStatutCompte historiqueStatutCompte = findById(id);
        if (historiqueStatutCompte != null) {
            em.remove(historiqueStatutCompte);
            return true;
        }
        return false;
    }

    /**
     * Compte le nombre total d'historiques de statut de compte
     */
    public long count() {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(h) FROM HistoriqueStatutCompte h", 
            Long.class
        );
        return query.getSingleResult();
    }

    /**
     * Compte le nombre d'historiques de statut pour un compte
     */
    public long countByCompte(Integer idCompte) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(h) FROM HistoriqueStatutCompte h WHERE h.idCompte = :idCompte", 
            Long.class
        );
        query.setParameter("idCompte", idCompte);
        return query.getSingleResult();
    }
}