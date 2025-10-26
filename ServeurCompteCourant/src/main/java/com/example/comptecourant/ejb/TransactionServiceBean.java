package com.example.comptecourant.ejb;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.comptecourant.exceptions.CompteCourantException;
import com.example.comptecourant.models.CompteCourant;
import com.example.comptecourant.models.StatutTransaction;
import com.example.comptecourant.models.Transaction;
import com.example.comptecourant.models.TypeTransaction;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Bean Stateless pour la gestion des transactions.
 * Logique métier basée sur le TransactionService original.
 */
@Stateless
public class TransactionServiceBean implements TransactionServiceRemote {

    private static final Logger LOGGER = Logger.getLogger(TransactionServiceBean.class.getName());

    @PersistenceContext(unitName = "CompteCourantPU")
    private EntityManager em;

    /**
     * Récupère toutes les transactions
     */
    @Override
    public List<Transaction> getAllTransactions() throws CompteCourantException {
        try {
            return em.createQuery("SELECT t FROM Transaction t", Transaction.class).getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de toutes les transactions", e);
            throw new CompteCourantException("Erreur lors de la récupération des transactions: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère les transactions d'un compte
     */
    @Override
    public List<Transaction> getTransactionsByCompte(Integer compteId) throws CompteCourantException {
        if (compteId == null || compteId <= 0) {
            throw new CompteCourantException("L'ID du compte est obligatoire");
        }

        try {
            // Vérifier que le compte existe
            CompteCourant compte = em.find(CompteCourant.class, compteId);
            if (compte == null) {
                throw new CompteCourantException("Compte introuvable avec ID: " + compteId);
            }

            return em.createQuery(
                "SELECT t FROM Transaction t WHERE t.compte.idCompte = :compteId ORDER BY t.dateTransaction DESC",
                Transaction.class
            ).setParameter("compteId", compteId).getResultList();
        } catch (CompteCourantException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transactions du compte " + compteId, e);
            throw new CompteCourantException("Erreur lors de la récupération des transactions: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère les transactions par statut
     */
    @Override
    public List<Transaction> getTransactionsByStatut(StatutTransaction statut) throws CompteCourantException {
        if (statut == null) {
            throw new CompteCourantException("Le statut de transaction est obligatoire");
        }

        try {
            return em.createQuery(
                "SELECT t FROM Transaction t WHERE t.statutTransaction = :statut ORDER BY t.dateTransaction DESC",
                Transaction.class
            ).setParameter("statut", statut).getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transactions par statut", e);
            throw new CompteCourantException("Erreur lors de la récupération des transactions: " + e.getMessage(), e);
        }
    }

    /**
     * ÉTAPE 1: Crée une demande de transaction (en attente de validation)
     */
    @Override
    public Transaction demanderTransaction(Transaction transaction) throws CompteCourantException {
        if (transaction == null) {
            throw new CompteCourantException("Les données de la transaction sont obligatoires");
        }

        validateTransactionData(transaction);

        try {
            // Vérifier que le compte existe
            CompteCourant compte = em.find(CompteCourant.class, transaction.getIdCompte());
            if (compte == null) {
                throw new CompteCourantException("Compte introuvable avec ID: " + transaction.getIdCompte());
            }

            // Définir la date si elle n'est pas spécifiée
            if (transaction.getDateTransaction() == null) {
                transaction.setDateTransaction(LocalDate.now());
            }

            // Toute nouvelle transaction commence en "en_attente"
            transaction.setStatutTransaction(StatutTransaction.EN_ATTENTE);

            em.persist(transaction);

            LOGGER.log(Level.INFO, "Demande de transaction créée: ID={0}, Compte={1}, Type={2}, Montant={3}",
                new Object[]{transaction.getIdTransaction(), transaction.getIdCompte(), transaction.getTypeTransaction(), transaction.getMontant()});

            return transaction;
        } catch (CompteCourantException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de la demande de transaction", e);
            throw new CompteCourantException("Erreur lors de la création de la demande de transaction: " + e.getMessage(), e);
        }
    }

    /**
     * ÉTAPE 2: Valide une transaction en attente (confirme ou refuse)
     */
    @Override
    public Transaction validerTransaction(Integer idTransaction, boolean approuver) throws CompteCourantException {
        if (idTransaction == null || idTransaction <= 0) {
            throw new CompteCourantException("L'ID de la transaction est obligatoire");
        }

        try {
            Transaction transaction = em.find(Transaction.class, idTransaction);
            if (transaction == null) {
                throw new CompteCourantException("Transaction introuvable avec ID: " + idTransaction);
            }

            if (transaction.getStatutTransaction() != StatutTransaction.EN_ATTENTE) {
                throw new CompteCourantException("Transaction non en attente. Statut actuel: " + transaction.getStatutTransaction());
            }

            // Appliquer la validation
            StatutTransaction nouveauStatut = approuver 
                ? StatutTransaction.CONFIRMEE 
                : StatutTransaction.REFUSEE;

            transaction.setStatutTransaction(nouveauStatut);

            // Si la transaction est approuvée, mettre à jour les soldes
            if (approuver) {
                CompteCourant compte = em.find(CompteCourant.class, transaction.getIdCompte());
                if (compte == null) {
                    throw new CompteCourantException("Compte introuvable avec ID: " + transaction.getIdCompte());
                }

                BigDecimal nouveauSolde;
                if (transaction.getTypeTransaction() == TypeTransaction.DEPOT) {
                    // Dépôt : ajouter le montant au solde
                    nouveauSolde = compte.getSolde().add(transaction.getMontant());
                } else {
                    // Retrait : soustraire le montant du solde
                    nouveauSolde = compte.getSolde().subtract(transaction.getMontant());

                    // Vérifier que le solde ne devient pas négatif
                    if (nouveauSolde.compareTo(BigDecimal.ZERO) < 0) {
                        throw new CompteCourantException("Solde insuffisant pour effectuer le retrait");
                    }
                }

                compte.setSolde(nouveauSolde);
                em.merge(compte);

                LOGGER.log(Level.INFO, "Solde du compte {0} mis à jour: {1}",
                    new Object[]{compte.getIdCompte(), nouveauSolde});
            }

            em.merge(transaction);

            String action = approuver ? "approuvée" : "refusée";
            LOGGER.log(Level.INFO, "Transaction {0}: ID={1}, Nouveau statut={2}",
                new Object[]{action, idTransaction, nouveauStatut});

            return transaction;
        } catch (CompteCourantException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la validation de la transaction " + idTransaction, e);
            throw new CompteCourantException("Erreur lors de la validation de la transaction: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère les transactions en attente de validation
     */
    @Override
    public List<Transaction> getTransactionsEnAttente() throws CompteCourantException {
        try {
            return em.createQuery(
                "SELECT t FROM Transaction t WHERE t.statutTransaction = :statut ORDER BY t.dateTransaction ASC",
                Transaction.class
            ).setParameter("statut", StatutTransaction.EN_ATTENTE).getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transactions en attente", e);
            throw new CompteCourantException("Erreur lors de la récupération des transactions en attente: " + e.getMessage(), e);
        }
    }

    /**
     * Validation des données de transaction
     */
    private void validateTransactionData(Transaction transaction) throws CompteCourantException {
        if (transaction.getMontant() == null || transaction.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CompteCourantException("Le montant doit être positif");
        }

        if (transaction.getIdCompte() == null || transaction.getIdCompte() <= 0) {
            throw new CompteCourantException("L'ID du compte est obligatoire");
        }

        if (transaction.getTypeTransaction() == null) {
            throw new CompteCourantException("Le type de transaction est obligatoire");
        }
    }
}
