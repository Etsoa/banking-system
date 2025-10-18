package com.example.serveurcomptecourant.repository;

import com.example.serveurcomptecourant.models.Transaction;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class TransactionRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Transaction> findAll() {
        return em.createQuery("SELECT t FROM Transaction t", Transaction.class)
                .getResultList();
    }

    public List<Transaction> findByCompteId(String compteId) {
        return em.createQuery("SELECT t FROM Transaction t WHERE t.idCompte = :compteId ORDER BY t.dateTransaction DESC", Transaction.class)
                .setParameter("compteId", compteId)
                .getResultList();
    }

    public List<Transaction> findByCompteIdAndTypeTransaction(String compteId, int typeId) {
        return em.createQuery("SELECT t FROM Transaction t WHERE t.idCompte = :compteId AND t.idTypeTransaction = :typeId ORDER BY t.dateTransaction DESC", Transaction.class)
                .setParameter("compteId", compteId)
                .setParameter("typeId", typeId)
                .getResultList();
    }

    public Transaction findById(int id) {
        return em.find(Transaction.class, id);
    }

    public Transaction save(Transaction transaction) {
        if (transaction.getId() == null) {
            em.persist(transaction);
            return transaction;
        } else {
            return em.merge(transaction);
        }
    }
}