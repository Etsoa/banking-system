package com.example.serveurpret.repository;

import com.example.serveurpret.models.Modalite;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ModaliteRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Modalite> findAll() {
        return em.createQuery("SELECT m FROM Modalite m", Modalite.class)
                .getResultList();
    }

    public List<Modalite> findActives() {
        return em.createQuery("SELECT m FROM Modalite m WHERE m.actif = true", Modalite.class)
                .getResultList();
    }

    public Modalite findById(Integer id) {
        return em.find(Modalite.class, id);
    }

    public Modalite save(Modalite modalite) {
        if (modalite.getId() == null) {
            em.persist(modalite);
            return modalite;
        } else {
            return em.merge(modalite);
        }
    }

    public void delete(Integer id) {
        Modalite modalite = findById(id);
        if (modalite != null) {
            em.remove(modalite);
        }
    }
}