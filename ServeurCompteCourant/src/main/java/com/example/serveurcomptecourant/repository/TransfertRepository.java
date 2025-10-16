package com.example.serveurcomptecourant.repository;

import com.example.serveurcomptecourant.models.Transfert;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Stateless
public class TransfertRepository {

    @PersistenceContext(unitName = "ServeurCompteCourantPU")
    private EntityManager em;

    public List<Transfert> findAll() {
        return em.createQuery("SELECT t FROM Transfert t", Transfert.class)
                .getResultList();
    }

    public List<Transfert> findByEnvoyer(String envoyerId) {
        return em.createQuery("SELECT t FROM Transfert t WHERE t.envoyer = :envoyerId", Transfert.class)
                .setParameter("envoyerId", envoyerId)
                .getResultList();
    }

    public List<Transfert> findByReceveur(String receveurId) {
        return em.createQuery("SELECT t FROM Transfert t WHERE t.receveur = :receveurId", Transfert.class)
                .setParameter("receveurId", receveurId)
                .getResultList();
    }

    public Transfert findById(Integer id) {
        return em.find(Transfert.class, id);
    }

    public Transfert save(Transfert transfert) {
        if (transfert.getId() == null) {
            em.persist(transfert);
            return transfert;
        } else {
            return em.merge(transfert);
        }
    }

    public void delete(Integer id) {
        Transfert transfert = findById(id);
        if (transfert != null) {
            em.remove(transfert);
        }
    }

    /**
     * Trouve tous les transferts par compte (envoyeur ou receveur)
     */
    public List<Transfert> findByCompte(String compteId) {
        TypedQuery<Transfert> query = em.createQuery(
            "SELECT t FROM Transfert t WHERE t.envoyer = :compteId OR t.receveur = :compteId ORDER BY t.dateTransfert DESC", 
            Transfert.class
        );
        query.setParameter("compteId", compteId);
        return query.getResultList();
    }

    /**
     * Trouve les transferts par montant minimum
     */
    public List<Transfert> findByMontantMin(BigDecimal montantMin) {
        TypedQuery<Transfert> query = em.createQuery(
            "SELECT t FROM Transfert t WHERE t.montant >= :montantMin ORDER BY t.dateTransfert DESC", 
            Transfert.class
        );
        query.setParameter("montantMin", montantMin);
        return query.getResultList();
    }

    /**
     * Trouve les transferts par période
     */
    public List<Transfert> findByDateRange(LocalDate dateDebut, LocalDate dateFin) {
        TypedQuery<Transfert> query = em.createQuery(
            "SELECT t FROM Transfert t WHERE t.dateTransfert BETWEEN :dateDebut AND :dateFin ORDER BY t.dateTransfert DESC", 
            Transfert.class
        );
        query.setParameter("dateDebut", dateDebut);
        query.setParameter("dateFin", dateFin);
        return query.getResultList();
    }

    /**
     * Trouve les transferts entre deux comptes spécifiques
     */
    public List<Transfert> findByEnvoyeurAndReceveur(String envoyeurId, String receveurId) {
        TypedQuery<Transfert> query = em.createQuery(
            "SELECT t FROM Transfert t WHERE t.envoyer = :envoyeurId AND t.receveur = :receveurId ORDER BY t.dateTransfert DESC", 
            Transfert.class
        );
        query.setParameter("envoyeurId", envoyeurId);
        query.setParameter("receveurId", receveurId);
        return query.getResultList();
    }

    /**
     * Trouve les transferts par transaction d'envoyeur
     */
    public List<Transfert> findByTransactionEnvoyeur(String idTransactionEnvoyeur) {
        TypedQuery<Transfert> query = em.createQuery(
            "SELECT t FROM Transfert t WHERE t.idTransactionEnvoyeur = :idTransactionEnvoyeur ORDER BY t.dateTransfert DESC", 
            Transfert.class
        );
        query.setParameter("idTransactionEnvoyeur", idTransactionEnvoyeur);
        return query.getResultList();
    }

    /**
     * Trouve les transferts par transaction de receveur
     */
    public List<Transfert> findByTransactionReceveur(String idTransactionReceveur) {
        TypedQuery<Transfert> query = em.createQuery(
            "SELECT t FROM Transfert t WHERE t.idTransactionReceveur = :idTransactionReceveur ORDER BY t.dateTransfert DESC", 
            Transfert.class
        );
        query.setParameter("idTransactionReceveur", idTransactionReceveur);
        return query.getResultList();
    }

    /**
     * Calcule le total des transferts sortants pour un compte
     */
    public BigDecimal getTotalTransfertsSortants(String compteId) {
        TypedQuery<BigDecimal> query = em.createQuery(
            "SELECT COALESCE(SUM(t.montant), 0) FROM Transfert t WHERE t.envoyer = :compteId", 
            BigDecimal.class
        );
        query.setParameter("compteId", compteId);
        return query.getSingleResult();
    }

    /**
     * Calcule le total des transferts entrants pour un compte
     */
    public BigDecimal getTotalTransfertsEntrants(String compteId) {
        TypedQuery<BigDecimal> query = em.createQuery(
            "SELECT COALESCE(SUM(t.montant), 0) FROM Transfert t WHERE t.receveur = :compteId", 
            BigDecimal.class
        );
        query.setParameter("compteId", compteId);
        return query.getSingleResult();
    }

    /**
     * Compte le nombre total de transferts
     */
    public long count() {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(t) FROM Transfert t", 
            Long.class
        );
        return query.getSingleResult();
    }

    /**
     * Compte le nombre de transferts pour un compte (envoyeur ou receveur)
     */
    public long countByCompte(String compteId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(t) FROM Transfert t WHERE t.envoyer = :compteId OR t.receveur = :compteId", 
            Long.class
        );
        query.setParameter("compteId", compteId);
        return query.getSingleResult();
    }

    /**
     * Trouve les transferts récents (derniers N jours)
     */
    public List<Transfert> findRecents(int nombreJours) {
        LocalDate dateDebut = LocalDate.now().minusDays(nombreJours);
        TypedQuery<Transfert> query = em.createQuery(
            "SELECT t FROM Transfert t WHERE t.dateTransfert >= :dateDebut ORDER BY t.dateTransfert DESC", 
            Transfert.class
        );
        query.setParameter("dateDebut", dateDebut);
        return query.getResultList();
    }

    /**
     * Vérifie si un transfert existe par ID
     */
    public boolean existsById(Integer id) {
        return findById(id) != null;
    }

    /**
     * Supprime un transfert par objet
     */
    public void delete(Transfert transfert) {
        em.remove(em.contains(transfert) ? transfert : em.merge(transfert));
    }
}