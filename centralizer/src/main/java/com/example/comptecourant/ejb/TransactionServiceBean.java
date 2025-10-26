package com.example.comptecourant.ejb;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
     * Convertit une entité Transaction en DTO
     */
    private com.example.centralizer.dto.comptecourant.Transaction convertToDTO(Transaction entity) {
        if (entity == null) {
            return null;
        }
        com.example.centralizer.dto.comptecourant.Transaction dto = new com.example.centralizer.dto.comptecourant.Transaction();
        dto.setIdTransaction(entity.getIdTransaction());
        dto.setMontant(entity.getMontant());
        dto.setDateTransaction(entity.getDateTransaction());
        dto.setStatutTransaction(com.example.centralizer.dto.comptecourant.StatutTransaction.valueOf(entity.getStatutTransaction().name()));
        dto.setTypeTransaction(com.example.centralizer.dto.comptecourant.TypeTransaction.valueOf(entity.getTypeTransaction().name()));
        if (entity.getCompte() != null) {
            dto.setIdCompte(entity.getCompte().getIdCompte());
        }
        if (entity.getCompteContrepartie() != null) {
            dto.setIdCompteContrepartie(entity.getCompteContrepartie().getIdCompte());
        }
        return dto;
    }

    /**
     * Convertit un DTO Transaction en entité
     */
    private Transaction convertToEntity(com.example.centralizer.dto.comptecourant.Transaction dto) {
        if (dto == null) {
            return null;
        }
        Transaction entity = new Transaction();
        entity.setIdTransaction(dto.getIdTransaction());
        entity.setMontant(dto.getMontant());
        entity.setDateTransaction(dto.getDateTransaction());
        entity.setStatutTransaction(StatutTransaction.valueOf(dto.getStatutTransaction().name()));
        entity.setTypeTransaction(TypeTransaction.valueOf(dto.getTypeTransaction().name()));
        if (dto.getIdCompte() != null) {
            CompteCourant compte = em.find(CompteCourant.class, dto.getIdCompte());
            entity.setCompte(compte);
        }
        if (dto.getIdCompteContrepartie() != null) {
            CompteCourant compteContrepartie = em.find(CompteCourant.class, dto.getIdCompteContrepartie());
            entity.setCompteContrepartie(compteContrepartie);
        }
        return entity;
    }

    /**
     * Récupère toutes les transactions
     */
    @Override
    public List<com.example.centralizer.dto.comptecourant.Transaction> getAllTransactions() throws CompteCourantException {
        try {
            List<Transaction> entities = em.createQuery("SELECT t FROM Transaction t", Transaction.class).getResultList();
            return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de toutes les transactions", e);
            throw new CompteCourantException("Erreur lors de la récupération des transactions: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère les transactions d'un compte
     */
    @Override
    public List<com.example.centralizer.dto.comptecourant.Transaction> getTransactionsByCompte(Integer compteId) throws CompteCourantException {
        if (compteId == null || compteId <= 0) {
            throw new CompteCourantException("L'ID du compte est obligatoire");
        }

        try {
            // Vérifier que le compte existe
            CompteCourant compte = em.find(CompteCourant.class, compteId);
            if (compte == null) {
                throw new CompteCourantException("Compte introuvable avec ID: " + compteId);
            }

            List<Transaction> entities = em.createQuery(
                "SELECT t FROM Transaction t WHERE t.compte.idCompte = :compteId ORDER BY t.dateTransaction DESC",
                Transaction.class
            ).setParameter("compteId", compteId).getResultList();
            
            return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
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
    public List<com.example.centralizer.dto.comptecourant.Transaction> getTransactionsByStatut(com.example.centralizer.dto.comptecourant.StatutTransaction statut) throws CompteCourantException {
        if (statut == null) {
            throw new CompteCourantException("Le statut de transaction est obligatoire");
        }

        try {
            List<Transaction> entities = em.createQuery(
                "SELECT t FROM Transaction t WHERE t.statutTransaction = :statut ORDER BY t.dateTransaction DESC",
                Transaction.class
            ).setParameter("statut", statut).getResultList();
            
            return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transactions par statut", e);
            throw new CompteCourantException("Erreur lors de la récupération des transactions: " + e.getMessage(), e);
        }
    }

    /**
     * ÉTAPE 1: Crée une demande de transaction (en attente de validation)
     */
    @Override
    public com.example.centralizer.dto.comptecourant.Transaction demanderTransaction(com.example.centralizer.dto.comptecourant.Transaction transaction) throws CompteCourantException {
        if (transaction == null) {
            throw new CompteCourantException("Les données de la transaction sont obligatoires");
        }

        Transaction entity = convertToEntity(transaction);
        validateTransactionData(entity);

        try {
            // Vérifier que le compte existe
            CompteCourant compte = em.find(CompteCourant.class, entity.getIdCompte());
            if (compte == null) {
                throw new CompteCourantException("Compte introuvable avec ID: " + entity.getIdCompte());
            }

            // Définir la date si elle n'est pas spécifiée
            if (entity.getDateTransaction() == null) {
                entity.setDateTransaction(LocalDate.now());
            }

            // Toute nouvelle transaction commence en "en_attente"
            entity.setStatutTransaction(StatutTransaction.en_attente);

            em.persist(entity);

            LOGGER.log(Level.INFO, "Demande de transaction créée: ID={0}, Compte={1}, Type={2}, Montant={3}",
                new Object[]{entity.getIdTransaction(), entity.getIdCompte(), entity.getTypeTransaction(), entity.getMontant()});

            return convertToDTO(entity);
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
    public com.example.centralizer.dto.comptecourant.Transaction validerTransaction(Integer idTransaction, boolean approuver) throws CompteCourantException {
        if (idTransaction == null || idTransaction <= 0) {
            throw new CompteCourantException("L'ID de la transaction est obligatoire");
        }

        try {
            Transaction transaction = em.find(Transaction.class, idTransaction);
            if (transaction == null) {
                throw new CompteCourantException("Transaction introuvable avec ID: " + idTransaction);
            }

            if (transaction.getStatutTransaction() != StatutTransaction.en_attente) {
                throw new CompteCourantException("Transaction non en attente. Statut actuel: " + transaction.getStatutTransaction());
            }

            // Appliquer la validation
            StatutTransaction nouveauStatut = approuver 
                ? StatutTransaction.confirmee 
                : StatutTransaction.refusee;

            transaction.setStatutTransaction(nouveauStatut);

            // Si la transaction est approuvée, mettre à jour les soldes
            if (approuver) {
                CompteCourant compte = em.find(CompteCourant.class, transaction.getIdCompte());
                if (compte == null) {
                    throw new CompteCourantException("Compte introuvable avec ID: " + transaction.getIdCompte());
                }

                BigDecimal nouveauSolde;
                if (transaction.getTypeTransaction() == TypeTransaction.depot) {
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

            return convertToDTO(transaction);
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
    public List<com.example.centralizer.dto.comptecourant.Transaction> getTransactionsEnAttente() throws CompteCourantException {
        try {
            List<Transaction> entities = em.createQuery(
                "SELECT t FROM Transaction t WHERE t.statutTransaction = :statut ORDER BY t.dateTransaction ASC",
                Transaction.class
            ).setParameter("statut", StatutTransaction.en_attente).getResultList();
            
            return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
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
