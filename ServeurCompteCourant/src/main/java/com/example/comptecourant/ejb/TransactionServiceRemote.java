package com.example.comptecourant.ejb;

import com.example.comptecourant.models.Transaction;
import com.example.comptecourant.models.StatutTransaction;
import com.example.comptecourant.exceptions.CompteCourantException;
import jakarta.ejb.Remote;
import java.util.List;

@Remote
public interface TransactionServiceRemote {
    List<Transaction> getAllTransactions() throws CompteCourantException;
    List<Transaction> getTransactionsByCompte(Integer compteId) throws CompteCourantException;
    List<Transaction> getTransactionsByStatut(StatutTransaction statut) throws CompteCourantException;
    Transaction demanderTransaction(Transaction transaction) throws CompteCourantException;
    Transaction validerTransaction(Integer idTransaction, boolean approuver) throws CompteCourantException;
    List<Transaction> getTransactionsEnAttente() throws CompteCourantException;
}
