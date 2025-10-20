package com.example.serveurcomptecourant.repository;

import com.example.serveurcomptecourant.models.Transaction;
import com.example.serveurcomptecourant.models.TypeTransaction;
import com.example.serveurcomptecourant.models.StatutTransaction;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class TransactionRepository {

    @PersistenceContext(unitName = "ServeurCompteCourantPU")
    private EntityManager em;

    public List<Transaction> findAll() {
        return em.createQuery("SELECT t FROM Transaction t ORDER BY t.dateTransaction DESC", Transaction.class)
                .getResultList();
    }

    public List<Transaction> findByCompteId(Integer compteId) {
        return em.createQuery("SELECT t FROM Transaction t WHERE t.idCompte = :compteId ORDER BY t.dateTransaction DESC", Transaction.class)
                .setParameter("compteId", compteId)
                .getResultList();
    }

    public List<Transaction> findByTypeTransaction(TypeTransaction typeTransaction) {
        return em.createQuery("SELECT t FROM Transaction t WHERE t.typeTransaction = :typeTransaction ORDER BY t.dateTransaction DESC", Transaction.class)
                .setParameter("typeTransaction", typeTransaction)
                .getResultList();
    }

    public List<Transaction> findByStatutTransaction(StatutTransaction statutTransaction) {
        return em.createQuery("SELECT t FROM Transaction t WHERE t.statutTransaction = :statutTransaction ORDER BY t.dateTransaction DESC", Transaction.class)
                .setParameter("statutTransaction", statutTransaction)
                .getResultList();
    }

    public List<Transaction> findByCompteIdAndStatut(Integer compteId, StatutTransaction statutTransaction) {
        return em.createQuery("SELECT t FROM Transaction t WHERE t.idCompte = :compteId AND t.statutTransaction = :statutTransaction ORDER BY t.dateTransaction DESC", Transaction.class)
                .setParameter("compteId", compteId)
                .setParameter("statutTransaction", statutTransaction)
                .getResultList();
    }

    public Transaction findById(Integer id) {
        return em.find(Transaction.class, id);
    }

    public Transaction save(Transaction transaction) {
        if (transaction.getIdTransaction() == null) {
            em.persist(transaction);
            return transaction;
        } else {
            return em.merge(transaction);
        }
    }

    public void delete(Transaction transaction) {
        em.remove(em.contains(transaction) ? transaction : em.merge(transaction));
    }
}