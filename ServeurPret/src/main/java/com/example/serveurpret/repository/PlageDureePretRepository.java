package com.example.serveurpret.repository;

import com.example.serveurpret.models.PlageDureePret;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class PlageDureePretRepository {

    @PersistenceContext
    private EntityManager em;

    public List<PlageDureePret> findAll() {
        return em.createQuery("SELECT p FROM PlageDureePret p", PlageDureePret.class)
                .getResultList();
    }

    public List<PlageDureePret> findActives() {
        return em.createQuery("SELECT p FROM PlageDureePret p WHERE p.actif = true", PlageDureePret.class)
                .getResultList();
    }

    public PlageDureePret findById(Integer id) {
        return em.find(PlageDureePret.class, id);
    }

    public PlageDureePret findByMontant(BigDecimal montant) {
        return em.createQuery("SELECT p FROM PlageDureePret p WHERE p.montantMin <= :montant AND p.montantMax >= :montant AND p.actif = true", PlageDureePret.class)
                .setParameter("montant", montant)
                .getSingleResult();
    }

    public List<PlageDureePret> findByMontantRange(BigDecimal montantMin, BigDecimal montantMax) {
        return em.createQuery("SELECT p FROM PlageDureePret p WHERE p.montantMax >= :montantMin AND p.montantMin <= :montantMax AND p.actif = true", PlageDureePret.class)
                .setParameter("montantMin", montantMin)
                .setParameter("montantMax", montantMax)
                .getResultList();
    }

    public PlageDureePret save(PlageDureePret plageDureePret) {
        if (plageDureePret.getId() == null) {
            em.persist(plageDureePret);
            return plageDureePret;
        } else {
            return em.merge(plageDureePret);
        }
    }

    public void delete(PlageDureePret plageDureePret) {
        em.remove(em.contains(plageDureePret) ? plageDureePret : em.merge(plageDureePret));
    }
}