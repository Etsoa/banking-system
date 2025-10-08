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

    public List<Transaction> findByCompteId(Integer compteId) {
        return em.createQuery("SELECT t FROM Transaction t WHERE t.idCompte = :compteId ORDER BY t.dateTransaction DESC", Transaction.class)
                .setParameter("compteId", compteId)
                .getResultList();
    }

    public List<Transaction> findByTransfertId(Integer transfertId) {
        return em.createQuery("SELECT t FROM Transaction t WHERE t.idTransfert = :transfertId", Transaction.class)
                .setParameter("transfertId", transfertId)
                .getResultList();
    }

    public Transaction findById(Integer id) {
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

    public void delete(Integer id) {
        Transaction transaction = findById(id);
        if (transaction != null) {
            em.remove(transaction);
        }
    }
}