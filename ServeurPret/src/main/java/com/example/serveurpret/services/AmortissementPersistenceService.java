package com.example.serveurpret.services;

import com.example.serveurpret.models.*;
import com.example.serveurpret.repository.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;

import java.util.List;
import java.util.logging.Logger;

@Stateless
public class AmortissementPersistenceService {
    private static final Logger LOGGER = Logger.getLogger(AmortissementPersistenceService.class.getName());

    @EJB
    private AmortissementPretRepository amortissementRepository;

    /**
     * Sauvegarde le tableau d'amortissement en base
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void sauvegarderAmortissement(List<AmortissementPret> amortissements) {
        try {
            for (AmortissementPret amortissement : amortissements) {
                amortissementRepository.save(amortissement);
            }
            
            LOGGER.info("Tableau d'amortissement sauvegardé : " + amortissements.size() + " lignes");
            
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la sauvegarde du tableau d'amortissement : " + e.getMessage());
            throw new RuntimeException("Erreur lors de la sauvegarde du tableau d'amortissement", e);
        }
    }

    /**
     * Supprime le tableau d'amortissement d'un prêt
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void supprimerAmortissement(Integer pretId) {
        try {
            // Utilisation de la méthode dédiée du repository
            amortissementRepository.deleteByIdPret(pretId);
            
            LOGGER.info("Tableau d'amortissement supprimé pour le prêt " + pretId);
            
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la suppression du tableau d'amortissement : " + e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression du tableau d'amortissement", e);
        }
    }

    /**
     * Récupère le tableau d'amortissement d'un prêt
     */
    public List<AmortissementPret> getAmortissementByPretId(Integer pretId) {
        if (pretId == null) {
            throw new IllegalArgumentException("L'ID du prêt ne peut pas être null");
        }
        
        return amortissementRepository.findByIdPretOrderByPeriode(pretId);
    }

    /**
     * Vérifie si un tableau d'amortissement existe pour un prêt
     */
    public boolean amortissementExists(Integer pretId) {
        if (pretId == null) {
            return false;
        }
        
        Long count = amortissementRepository.countByIdPret(pretId);
        return count > 0;
    }
}