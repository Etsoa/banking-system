package com.example.serveurpret.repository;

import com.example.serveurpret.models.StatutPret;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class StatutPretRepository {

    @PersistenceContext
    private EntityManager em;

    public List<StatutPret> findAll() {
        return em.createQuery("SELECT s FROM StatutPret s", StatutPret.class)
                .getResultList();
    }

    public List<StatutPret> findActifs() {
        return em.createQuery("SELECT s FROM StatutPret s WHERE s.actif = true", StatutPret.class)
                .getResultList();
    }

    public StatutPret findById(Integer id) {
        return em.find(StatutPret.class, id);
    }

    public StatutPret save(StatutPret statutPret) {
        if (statutPret.getId() == null) {
            em.persist(statutPret);
            return statutPret;
        } else {
            return em.merge(statutPret);
        }
    }

    public void delete(Integer id) {
        StatutPret statutPret = findById(id);
        if (statutPret != null) {
            em.remove(statutPret);
        }
    }
}