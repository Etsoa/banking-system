package com.example.serveurpret.repository;

import com.example.serveurpret.models.Pret;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class PretRepository {

    @PersistenceContext(unitName = "ServeurPretPU")
    private EntityManager em;

    public List<Pret> findAll() {
        return em.createQuery("SELECT p FROM Pret p", Pret.class).getResultList();
    }

    public Pret findById(Integer id) {
        return em.find(Pret.class, id);
    }

    public void save(Pret pret) {
        em.persist(pret);
    }

    public Pret update(Pret pret) {
        return em.merge(pret);
    }

    public void delete(Pret pret) {
        em.remove(em.contains(pret) ? pret : em.merge(pret));
    }
}
