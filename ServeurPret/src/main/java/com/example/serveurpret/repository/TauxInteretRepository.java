package com.example.serveurpret.repository;

import com.example.serveurpret.models.TauxInteret;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

@Stateless
public class TauxInteretRepository {

    @PersistenceContext
    private EntityManager em;

    public List<TauxInteret> findAll() {
        return em.createQuery("SELECT t FROM TauxInteret t ORDER BY t.dateDebut DESC", TauxInteret.class)
                .getResultList();
    }

    public TauxInteret findById(Integer id) {
        return em.find(TauxInteret.class, id);
    }

    public TauxInteret findCurrentTaux() {
        return em.createQuery("SELECT t FROM TauxInteret t WHERE t.dateDebut <= :today ORDER BY t.dateDebut DESC", TauxInteret.class)
                .setParameter("today", LocalDate.now())
                .setMaxResults(1)
                .getSingleResult();
    }

    public TauxInteret findTauxAtDate(LocalDate date) {
        return em.createQuery("SELECT t FROM TauxInteret t WHERE t.dateDebut <= :date ORDER BY t.dateDebut DESC", TauxInteret.class)
                .setParameter("date", date)
                .setMaxResults(1)
                .getSingleResult();
    }

    public List<TauxInteret> findByDateRange(LocalDate dateDebut, LocalDate dateFin) {
        return em.createQuery("SELECT t FROM TauxInteret t WHERE t.dateDebut >= :dateDebut AND t.dateDebut <= :dateFin ORDER BY t.dateDebut", TauxInteret.class)
                .setParameter("dateDebut", dateDebut)
                .setParameter("dateFin", dateFin)
                .getResultList();
    }

    public TauxInteret save(TauxInteret tauxInteret) {
        if (tauxInteret.getId() == null) {
            em.persist(tauxInteret);
            return tauxInteret;
        } else {
            return em.merge(tauxInteret);
        }
    }

    public void delete(TauxInteret tauxInteret) {
        em.remove(em.contains(tauxInteret) ? tauxInteret : em.merge(tauxInteret));
    }
}