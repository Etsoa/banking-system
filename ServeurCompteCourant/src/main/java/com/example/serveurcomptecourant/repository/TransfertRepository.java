package com.example.serveurcomptecourant.repository;

import com.example.serveurcomptecourant.models.Transfert;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class TransfertRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Transfert> findAll() {
        return em.createQuery("SELECT t FROM Transfert t", Transfert.class)
                .getResultList();
    }

    public List<Transfert> findByEnvoyer(Integer envoyerId) {
        return em.createQuery("SELECT t FROM Transfert t WHERE t.envoyer = :envoyerId", Transfert.class)
                .setParameter("envoyerId", envoyerId)
                .getResultList();
    }

    public List<Transfert> findByReceveur(Integer receveurId) {
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
}