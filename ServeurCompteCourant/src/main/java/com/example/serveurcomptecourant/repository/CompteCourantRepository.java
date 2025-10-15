package com.example.serveurcomptecourant.repository;

import com.example.serveurcomptecourant.models.CompteCourant;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class CompteCourantRepository {
    @PersistenceContext(unitName = "ServeurCompteCourantPU")
    private EntityManager em;

    public void save(CompteCourant compte) {
        if (compte.getId() == null) {
            em.persist(compte);
        } else {
            em.merge(compte);
        }
    }

    public CompteCourant find(long id) {
        return em.find(CompteCourant.class, (int)id);
    }

    public List<CompteCourant> findAll() {
        return em.createQuery("SELECT c FROM CompteCourant c", CompteCourant.class).getResultList();
    }

    public List<CompteCourant> findByClientId(long clientId) {
        return em.createQuery("SELECT c FROM CompteCourant c WHERE c.idClient = :clientId", CompteCourant.class)
                .setParameter("clientId", (int)clientId)
                .getResultList();
    }

    public String getCurrentStatut(int compteId) {
        try {
            String statutLibelle = em.createQuery(
                "SELECT ts.libelle FROM HistoriqueStatutCompte h " +
                "JOIN TypeStatutCompte ts ON h.idTypeStatutCompte = ts.idTypeStatutCompte " +
                "WHERE h.idCompte = :compteId " +
                "ORDER BY h.dateChangement DESC", String.class)
                .setParameter("compteId", compteId)
                .setMaxResults(1)
                .getSingleResult();
            return statutLibelle;
        } catch (Exception e) {
            return "Actif"; // Statut par défaut si aucun historique
        }
    }

    public void delete(CompteCourant compte) {
        em.remove(em.contains(compte) ? compte : em.merge(compte));
    }
}
