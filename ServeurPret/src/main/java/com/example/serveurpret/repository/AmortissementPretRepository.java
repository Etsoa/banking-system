package com.example.serveurpret.repository;

import com.example.serveurpret.models.AmortissementPret;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class AmortissementPretRepository {

    @PersistenceContext
    private EntityManager em;

    public List<AmortissementPret> findAll() {
        return em.createQuery("SELECT a FROM AmortissementPret a", AmortissementPret.class)
                .getResultList();
    }

    public List<AmortissementPret> findByIdPretOrderByPeriode(Integer idPret) {
        return em.createQuery("SELECT a FROM AmortissementPret a WHERE a.idPret = :idPret ORDER BY a.periode", AmortissementPret.class)
                .setParameter("idPret", idPret)
                .getResultList();
    }

    public AmortissementPret findById(Integer id) {
        return em.find(AmortissementPret.class, id);
    }

    public AmortissementPret findByIdPretAndPeriode(Integer idPret, Integer periode) {
        return em.createQuery("SELECT a FROM AmortissementPret a WHERE a.idPret = :idPret AND a.periode = :periode", AmortissementPret.class)
                .setParameter("idPret", idPret)
                .setParameter("periode", periode)
                .getSingleResult();
    }

    public Long countByIdPret(Integer idPret) {
        return em.createQuery("SELECT COUNT(a) FROM AmortissementPret a WHERE a.idPret = :idPret", Long.class)
                .setParameter("idPret", idPret)
                .getSingleResult();
    }

    public AmortissementPret save(AmortissementPret amortissementPret) {
        if (amortissementPret.getId() == null) {
            em.persist(amortissementPret);
            return amortissementPret;
        } else {
            return em.merge(amortissementPret);
        }
    }

    public void delete(AmortissementPret amortissementPret) {
        em.remove(em.contains(amortissementPret) ? amortissementPret : em.merge(amortissementPret));
    }

    public void deleteByIdPret(Integer idPret) {
        em.createQuery("DELETE FROM AmortissementPret a WHERE a.idPret = :idPret")
                .setParameter("idPret", idPret)
                .executeUpdate();
    }
}