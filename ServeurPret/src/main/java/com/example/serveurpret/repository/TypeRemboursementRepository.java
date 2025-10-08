package com.example.serveurpret.repository;

import com.example.serveurpret.models.TypeRemboursement;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class TypeRemboursementRepository {

    @PersistenceContext
    private EntityManager em;

    public List<TypeRemboursement> findAll() {
        return em.createQuery("SELECT t FROM TypeRemboursement t", TypeRemboursement.class)
                .getResultList();
    }

    public List<TypeRemboursement> findActifs() {
        return em.createQuery("SELECT t FROM TypeRemboursement t WHERE t.actif = true", TypeRemboursement.class)
                .getResultList();
    }

    public TypeRemboursement findById(Integer id) {
        return em.find(TypeRemboursement.class, id);
    }

    public TypeRemboursement save(TypeRemboursement typeRemboursement) {
        if (typeRemboursement.getId() == null) {
            em.persist(typeRemboursement);
            return typeRemboursement;
        } else {
            return em.merge(typeRemboursement);
        }
    }

    public void delete(Integer id) {
        TypeRemboursement typeRemboursement = findById(id);
        if (typeRemboursement != null) {
            em.remove(typeRemboursement);
        }
    }
}