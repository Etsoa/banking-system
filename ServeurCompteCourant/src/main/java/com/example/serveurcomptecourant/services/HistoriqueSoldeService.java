package com.example.serveurcomptecourant.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.example.serveurcomptecourant.exceptions.CompteCourantBusinessException;
import com.example.serveurcomptecourant.exceptions.CompteCourantException;
import com.example.serveurcomptecourant.models.CompteCourant;
import com.example.serveurcomptecourant.models.HistoriqueSolde;
import com.example.serveurcomptecourant.repository.HistoriqueSoldeRepository;
import com.example.serveurcomptecourant.repository.CompteCourantRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;

@Stateless
public class HistoriqueSoldeService {
    private static final Logger LOGGER = Logger.getLogger(HistoriqueSoldeService.class.getName());
    
    @EJB
    private HistoriqueSoldeRepository historiqueSoldeRepository;
    
    @EJB
    private CompteCourantRepository compteRepository;

    /**
     * Récupère tous les historiques de solde
     */
    public List<HistoriqueSolde> getAllHistoriquesSolde() throws CompteCourantException {
        try {
            return historiqueSoldeRepository.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des historiques de solde", e);
            throw new CompteCourantException("Erreur lors de la récupération des historiques de solde", e);
        }
    }

    /**
     * Récupère l'historique des soldes d'un compte
     */
    public List<HistoriqueSolde> getHistoriqueSoldeByCompte(String compteId) throws CompteCourantException {
        try {
            if (compteId == null || compteId.trim().isEmpty()) {
                throw new CompteCourantBusinessException("L'ID du compte est obligatoire");
            }
            
            // Vérifier que le compte existe
            CompteCourant compte = compteRepository.find(compteId);
            if (compte == null) {
                throw new CompteCourantBusinessException.CompteNotFoundException(0L);
            }
            
            return historiqueSoldeRepository.findByCompte(compteId);
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de l'historique du compte " + compteId, e);
            throw new CompteCourantException("Erreur lors de la récupération de l'historique", e);
        }
    }

    /**
     * Récupère l'historique des soldes d'un compte par période
     */
    public List<HistoriqueSolde> getHistoriqueSoldeByCompteAndPeriode(String compteId, LocalDateTime dateDebut, LocalDateTime dateFin) throws CompteCourantException {
        try {
            if (compteId == null || compteId.trim().isEmpty()) {
                throw new CompteCourantBusinessException("L'ID du compte est obligatoire");
            }
            
            if (dateDebut == null || dateFin == null) {
                throw new CompteCourantBusinessException("Les dates de début et fin sont obligatoires");
            }
            
            if (dateDebut.isAfter(dateFin)) {
                throw new CompteCourantBusinessException("La date de début doit être antérieure à la date de fin");
            }
            
            // Vérifier que le compte existe
            CompteCourant compte = compteRepository.find(compteId);
            if (compte == null) {
                throw new CompteCourantBusinessException.CompteNotFoundException(0L);
            }
            
            // Récupérer tous les historiques du compte et filtrer par date
            List<HistoriqueSolde> tousHistoriques = historiqueSoldeRepository.findByCompte(compteId);
            return tousHistoriques.stream()
                .filter(h -> h.getDateChangement().isAfter(dateDebut) && h.getDateChangement().isBefore(dateFin))
                .collect(Collectors.toList());
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de l'historique par période", e);
            throw new CompteCourantException("Erreur lors de la récupération de l'historique", e);
        }
    }

    /**
     * Récupère l'historique de solde par transaction
     */
    public HistoriqueSolde getHistoriqueSoldeByTransaction(Integer transactionId) throws CompteCourantException {
        try {
            if (transactionId == null || transactionId <= 0) {
                throw new CompteCourantBusinessException("L'ID de la transaction est obligatoire");
            }
            
            List<HistoriqueSolde> results = historiqueSoldeRepository.findByTransaction(transactionId);
            return results.isEmpty() ? null : results.get(0);
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de l'historique par transaction " + transactionId, e);
            throw new CompteCourantException("Erreur lors de la récupération de l'historique", e);
        }
    }

    /**
     * Récupère le dernier solde d'un compte
     */
    public HistoriqueSolde getDernierSolde(String compteId) throws CompteCourantException {
        try {
            if (compteId == null || compteId.trim().isEmpty()) {
                throw new CompteCourantBusinessException("L'ID du compte est obligatoire");
            }
            
            // Vérifier que le compte existe
            CompteCourant compte = compteRepository.find(compteId);
            if (compte == null) {
                throw new CompteCourantBusinessException.CompteNotFoundException(0L);
            }
            
            return historiqueSoldeRepository.findLastByCompte(compteId);
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du dernier solde du compte " + compteId, e);
            throw new CompteCourantException("Erreur lors de la récupération du dernier solde", e);
        }
    }

    /**
     * Crée un nouvel historique de solde (appelé lors des transactions)
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public HistoriqueSolde createHistoriqueSolde(BigDecimal nouveauSolde, String compteId, Integer transactionId) throws CompteCourantException {
        try {
            // Validation des données
            validateHistoriqueSoldeData(nouveauSolde, compteId, transactionId);
            
            // Vérifier que le compte existe
            CompteCourant compte = compteRepository.find(compteId);
            if (compte == null) {
                throw new CompteCourantBusinessException.CompteNotFoundException(0L);
            }
            
            // Créer l'historique de solde
            HistoriqueSolde historiqueSolde = new HistoriqueSolde(nouveauSolde, compteId, transactionId);
            
            return historiqueSoldeRepository.save(historiqueSolde);
            
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de l'historique de solde", e);
            throw new CompteCourantException("Erreur lors de la création de l'historique de solde", e);
        }
    }

    /**
     * Calcule l'évolution du solde d'un compte sur une période
     */
    public BigDecimal getEvolutionSolde(String compteId, LocalDateTime dateDebut, LocalDateTime dateFin) throws CompteCourantException {
        try {
            if (compteId == null || compteId.trim().isEmpty()) {
                throw new CompteCourantBusinessException("L'ID du compte est obligatoire");
            }
            
            if (dateDebut == null || dateFin == null) {
                throw new CompteCourantBusinessException("Les dates de début et fin sont obligatoires");
            }
            
            if (dateDebut.isAfter(dateFin)) {
                throw new CompteCourantBusinessException("La date de début doit être antérieure à la date de fin");
            }
            
            List<HistoriqueSolde> historiques = getHistoriqueSoldeByCompteAndPeriode(compteId, dateDebut, dateFin);
            
            if (historiques.isEmpty()) {
                return BigDecimal.ZERO;
            }
            
            // Trouver le premier et le dernier solde de la période
            HistoriqueSolde premier = historiques.get(0);
            HistoriqueSolde dernier = historiques.get(historiques.size() - 1);
            
            return dernier.getMontant().subtract(premier.getMontant());
            
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du calcul de l'évolution du solde", e);
            throw new CompteCourantException("Erreur lors du calcul de l'évolution", e);
        }
    }

    /**
     * Récupère le solde minimum atteint sur une période
     */
    public BigDecimal getSoldeMinimum(String compteId, LocalDateTime dateDebut, LocalDateTime dateFin) throws CompteCourantException {
        try {
            List<HistoriqueSolde> historiques = getHistoriqueSoldeByCompteAndPeriode(compteId, dateDebut, dateFin);
            
            if (historiques.isEmpty()) {
                return BigDecimal.ZERO;
            }
            
            return historiques.stream()
                .map(HistoriqueSolde::getMontant)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
                
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du calcul du solde minimum", e);
            throw new CompteCourantException("Erreur lors du calcul du solde minimum", e);
        }
    }

    /**
     * Récupère le solde maximum atteint sur une période
     */
    public BigDecimal getSoldeMaximum(String compteId, LocalDateTime dateDebut, LocalDateTime dateFin) throws CompteCourantException {
        try {
            List<HistoriqueSolde> historiques = getHistoriqueSoldeByCompteAndPeriode(compteId, dateDebut, dateFin);
            
            if (historiques.isEmpty()) {
                return BigDecimal.ZERO;
            }
            
            return historiques.stream()
                .map(HistoriqueSolde::getMontant)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
                
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du calcul du solde maximum", e);
            throw new CompteCourantException("Erreur lors du calcul du solde maximum", e);
        }
    }

    /**
     * Validation des données d'historique de solde
     */
    private void validateHistoriqueSoldeData(BigDecimal nouveauSolde, String compteId, Integer transactionId) throws CompteCourantBusinessException {
        if (nouveauSolde == null) {
            throw new CompteCourantBusinessException("Le montant du nouveau solde est obligatoire");
        }
        
        if (compteId == null || compteId.trim().isEmpty()) {
            throw new CompteCourantBusinessException("L'ID du compte est obligatoire");
        }
        
        if (transactionId == null || transactionId <= 0) {
            throw new CompteCourantBusinessException("L'ID de la transaction est obligatoire");
        }
    }
}