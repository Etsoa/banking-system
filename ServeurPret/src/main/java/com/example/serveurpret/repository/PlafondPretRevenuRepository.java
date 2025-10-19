package com.example.serveurpret.repository;

import com.example.serveurpret.models.PlafondPretRevenu;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class PlafondPretRevenuRepository {
    private static final Logger LOGGER = Logger.getLogger(PlafondPretRevenuRepository.class.getName());

    @PersistenceContext(unitName = "ServeurPretPU")
    private EntityManager em;

    public PlafondPretRevenu findById(Integer id) {
        return em.find(PlafondPretRevenu.class, id);
    }

    public List<PlafondPretRevenu> findAll() {
        TypedQuery<PlafondPretRevenu> query = em.createQuery(
            "SELECT p FROM PlafondPretRevenu p ORDER BY p.revenuMin", 
            PlafondPretRevenu.class
        );
        return query.getResultList();
    }

    /**
     * Trouve le plafond de prêt applicable pour un revenu donné
     * en utilisant la règle la plus récente
     */
    public PlafondPretRevenu findByRevenu(BigDecimal revenu) {
        try {
            TypedQuery<PlafondPretRevenu> query = em.createQuery(
                "SELECT p FROM PlafondPretRevenu p " +
                "WHERE p.revenuMin <= :revenu AND p.revenuMax >= :revenu " +
                "ORDER BY p.dateDebut DESC",
                PlafondPretRevenu.class
            );
            query.setParameter("revenu", revenu);
            query.setMaxResults(1);
            
            List<PlafondPretRevenu> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la recherche du plafond pour le revenu " + revenu + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Trouve tous les plafonds actuellement en vigueur (les plus récents pour chaque tranche)
     */
    public List<PlafondPretRevenu> findCurrent() {
        TypedQuery<PlafondPretRevenu> query = em.createQuery(
            "SELECT p FROM PlafondPretRevenu p " +
            "WHERE p.dateDebut = (" +
            "   SELECT MAX(p2.dateDebut) FROM PlafondPretRevenu p2 " +
            "   WHERE p2.revenuMin = p.revenuMin AND p2.revenuMax = p.revenuMax" +
            ") ORDER BY p.revenuMin",
            PlafondPretRevenu.class
        );
        return query.getResultList();
    }

    /**
     * Trouve le plafond actuel pour un revenu donné
     */
    public PlafondPretRevenu findCurrentByRevenu(BigDecimal revenu) {
        try {
            TypedQuery<PlafondPretRevenu> query = em.createQuery(
                "SELECT p FROM PlafondPretRevenu p " +
                "WHERE p.revenuMin <= :revenu AND p.revenuMax >= :revenu " +
                "AND p.dateDebut <= :now " +
                "ORDER BY p.dateDebut DESC",
                PlafondPretRevenu.class
            );
            query.setParameter("revenu", revenu);
            query.setParameter("now", LocalDateTime.now());
            query.setMaxResults(1);
            
            List<PlafondPretRevenu> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la recherche du plafond actuel pour le revenu " + revenu + ": " + e.getMessage());
            return null;
        }
    }

    public void create(PlafondPretRevenu plafond) {
        em.persist(plafond);
    }

    public void update(PlafondPretRevenu plafond) {
        em.merge(plafond);
    }

    public void delete(Integer id) {
        PlafondPretRevenu plafond = findById(id);
        if (plafond != null) {
            em.remove(plafond);
        }
    }
}
