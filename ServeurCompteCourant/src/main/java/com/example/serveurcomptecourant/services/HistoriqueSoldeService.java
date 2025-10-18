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