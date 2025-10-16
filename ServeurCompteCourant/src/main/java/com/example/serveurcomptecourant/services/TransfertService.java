package com.example.serveurcomptecourant.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.serveurcomptecourant.exceptions.CompteCourantBusinessException;
import com.example.serveurcomptecourant.exceptions.CompteCourantException;
import com.example.serveurcomptecourant.models.CompteCourant;
import com.example.serveurcomptecourant.models.Transfert;
import com.example.serveurcomptecourant.models.Transaction;
import com.example.serveurcomptecourant.repository.TransfertRepository;
import com.example.serveurcomptecourant.repository.CompteCourantRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;

@Stateless
public class TransfertService {
    private static final Logger LOGGER = Logger.getLogger(TransfertService.class.getName());
    
    @EJB
    private TransfertRepository transfertRepository;
    
    @EJB
    private CompteCourantRepository compteRepository;

    /**
     * Récupère tous les transferts
     */
    public List<Transfert> getAllTransferts() throws CompteCourantException {
        try {
            return transfertRepository.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transferts", e);
            throw new CompteCourantException("Erreur lors de la récupération des transferts", e);
        }
    }

    /**
     * Récupère les transferts d'un compte (envoyeur ou receveur)
     */
    public List<Transfert> getTransfertsByCompte(String compteId) throws CompteCourantException {
        try {
            if (compteId == null || compteId.trim().isEmpty()) {
                throw new CompteCourantBusinessException("L'ID du compte est obligatoire");
            }
            
            // Vérifier que le compte existe
            CompteCourant compte = compteRepository.find(compteId);
            if (compte == null) {
                throw new CompteCourantBusinessException.CompteNotFoundException(0L);
            }
            
            return transfertRepository.findByCompte(compteId);
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transferts du compte " + compteId, e);
            throw new CompteCourantException("Erreur lors de la récupération des transferts", e);
        }
    }

    /**
     * Récupère les transferts envoyés par un compte
     */
    public List<Transfert> getTransfertsEnvoyes(String compteEnvoyeur) throws CompteCourantException {
        try {
            if (compteEnvoyeur == null || compteEnvoyeur.trim().isEmpty()) {
                throw new CompteCourantBusinessException("L'ID du compte envoyeur est obligatoire");
            }
            
            return transfertRepository.findByEnvoyer(compteEnvoyeur);
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transferts envoyés par " + compteEnvoyeur, e);
            throw new CompteCourantException("Erreur lors de la récupération des transferts", e);
        }
    }

    /**
     * Récupère les transferts reçus par un compte
     */
    public List<Transfert> getTransfertsRecus(String compteReceveur) throws CompteCourantException {
        try {
            if (compteReceveur == null || compteReceveur.trim().isEmpty()) {
                throw new CompteCourantBusinessException("L'ID du compte receveur est obligatoire");
            }
            
            return transfertRepository.findByReceveur(compteReceveur);
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transferts reçus par " + compteReceveur, e);
            throw new CompteCourantException("Erreur lors de la récupération des transferts", e);
        }
    }

    /**
     * Récupère les transferts par période
     */
    public List<Transfert> getTransfertsByPeriode(LocalDate dateDebut, LocalDate dateFin) throws CompteCourantException {
        try {
            if (dateDebut == null || dateFin == null) {
                throw new CompteCourantBusinessException("Les dates de début et fin sont obligatoires");
            }
            
            if (dateDebut.isAfter(dateFin)) {
                throw new CompteCourantBusinessException("La date de début doit être antérieure à la date de fin");
            }
            
            return transfertRepository.findByDateRange(dateDebut, dateFin);
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transferts par période", e);
            throw new CompteCourantException("Erreur lors de la récupération des transferts", e);
        }
    }

    /**
     * Récupère un transfert par ID
     */
    public Transfert getTransfertById(Integer id) throws CompteCourantException {
        try {
            if (id == null || id <= 0) {
                throw new CompteCourantBusinessException("L'ID du transfert est obligatoire");
            }
            
            Transfert transfert = transfertRepository.findById(id);
            if (transfert == null) {
                throw new CompteCourantBusinessException("Transfert introuvable");
            }
            
            return transfert;
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du transfert " + id, e);
            throw new CompteCourantException("Erreur lors de la récupération du transfert", e);
        }
    }

    /**
     * Crée un transfert (appelé par TransactionService)
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Transfert createTransfert(String compteEnvoyeur, String compteReceveur, BigDecimal montant, 
                                   Transaction transactionSortante, Transaction transactionEntrante) throws CompteCourantException {
        try {
            // Validation des paramètres
            validateTransfertData(compteEnvoyeur, compteReceveur, montant, transactionSortante, transactionEntrante);
            
            // Créer le transfert
            Transfert transfert = new Transfert();
            transfert.setMontant(montant);
            transfert.setEnvoyer(compteEnvoyeur);
            transfert.setReceveur(compteReceveur);
            transfert.setIdTransactionEnvoyeur(transactionSortante.getId().toString());
            transfert.setIdTransactionReceveur(transactionEntrante.getId().toString());
            
            return transfertRepository.save(transfert);
            
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création du transfert", e);
            throw new CompteCourantException("Erreur lors de la création du transfert", e);
        }
    }

    /**
     * Calcule le total des transferts envoyés par un compte
     */
    public BigDecimal getTotalTransfertsEnvoyes(String compteEnvoyeur) throws CompteCourantException {
        try {
            if (compteEnvoyeur == null || compteEnvoyeur.trim().isEmpty()) {
                throw new CompteCourantBusinessException("L'ID du compte envoyeur est obligatoire");
            }
            
            return transfertRepository.getTotalTransfertsSortants(compteEnvoyeur);
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du calcul du total des transferts envoyés", e);
            throw new CompteCourantException("Erreur lors du calcul du total", e);
        }
    }

    /**
     * Calcule le total des transferts reçus par un compte
     */
    public BigDecimal getTotalTransfertsRecus(String compteReceveur) throws CompteCourantException {
        try {
            if (compteReceveur == null || compteReceveur.trim().isEmpty()) {
                throw new CompteCourantBusinessException("L'ID du compte receveur est obligatoire");
            }
            
            return transfertRepository.getTotalTransfertsEntrants(compteReceveur);
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du calcul du total des transferts reçus", e);
            throw new CompteCourantException("Erreur lors du calcul du total", e);
        }
    }

    /**
     * Validation des données de transfert
     */
    private void validateTransfertData(String compteEnvoyeur, String compteReceveur, BigDecimal montant,
                                     Transaction transactionSortante, Transaction transactionEntrante) throws CompteCourantBusinessException {
        if (compteEnvoyeur == null || compteEnvoyeur.trim().isEmpty()) {
            throw new CompteCourantBusinessException("Le compte envoyeur est obligatoire");
        }
        
        if (compteReceveur == null || compteReceveur.trim().isEmpty()) {
            throw new CompteCourantBusinessException("Le compte receveur est obligatoire");
        }
        
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CompteCourantBusinessException.MontantInvalideException(
                montant != null ? montant.doubleValue() : 0.0
            );
        }
        
        if (compteEnvoyeur.equals(compteReceveur)) {
            throw new CompteCourantBusinessException("Le compte envoyeur et receveur doivent être différents");
        }
        
        if (transactionSortante == null || transactionEntrante == null) {
            throw new CompteCourantBusinessException("Les transactions associées sont obligatoires");
        }
        
        if (transactionSortante.getId() == null || transactionEntrante.getId() == null) {
            throw new CompteCourantBusinessException("Les transactions doivent être sauvegardées avant de créer le transfert");
        }
    }
}