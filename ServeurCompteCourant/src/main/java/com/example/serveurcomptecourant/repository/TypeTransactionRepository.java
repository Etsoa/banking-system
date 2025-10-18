package com.example.serveurcomptecourant.repository;

import com.example.serveurcomptecourant.models.TypeTransaction;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class TypeTransactionRepository {

    @PersistenceContext
    private EntityManager em;

    public List<TypeTransaction> findAll() {
        return em.createQuery("SELECT t FROM TypeTransaction t", TypeTransaction.class)
                .getResultList();
    }

    public List<TypeTransaction> findActifs() {
        return em.createQuery("SELECT t FROM TypeTransaction t WHERE t.actif = true", TypeTransaction.class)
                .getResultList();
    }

    public TypeTransaction findById(Integer id) {
        return em.find(TypeTransaction.class, id);
    }

    public TypeTransaction save(TypeTransaction typeTransaction) {
        if (typeTransaction.getId() == null) {
            em.persist(typeTransaction);
            return typeTransaction;
        } else {
            return em.merge(typeTransaction);
        }
    }
}