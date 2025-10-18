package com.example.serveurpret.services;

import com.example.serveurpret.models.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class AmortissementService {
    private static final Logger LOGGER = Logger.getLogger(AmortissementService.class.getName());

    @EJB
    private TauxInteretService tauxInteretService;

    @EJB
    private CalculAmortissementService calculAmortissementService;

    @EJB
    private AmortissementPersistenceService persistenceService;

    /**
     * Génère le tableau d'amortissement pour un prêt
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void genererAmortissement(Pret pret, TypeRemboursement typeRemboursement, Modalite modalite) {
        try {
            // Récupérer le taux d'intérêt actuel
            TauxInteret tauxActuel = tauxInteretService.getTauxActuel();
            BigDecimal tauxPeriodique = tauxInteretService.calculerTauxPeriodique(
                tauxActuel.getTauxAnnuel(), modalite.getNombreMois());

            List<AmortissementPret> amortissements;

            if ("Annuite constante".equalsIgnoreCase(typeRemboursement.getNom())) {
                amortissements = calculAmortissementService.genererAnnuiteConstante(pret, tauxPeriodique, modalite);
            } else if ("Amortissement constante".equalsIgnoreCase(typeRemboursement.getNom())) {
                amortissements = calculAmortissementService.genererAmortissementConstant(pret, tauxPeriodique, modalite);
            } else {
                throw new RuntimeException("Type de remboursement non reconnu : " + typeRemboursement.getNom());
            }

            // Sauvegarder le tableau d'amortissement
            persistenceService.sauvegarderAmortissement(amortissements);

            LOGGER.info("Tableau d'amortissement généré pour le prêt " + pret.getId() + " avec " + amortissements.size() + " périodes");

        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la génération du tableau d'amortissement : " + e.getMessage());
            throw new RuntimeException("Erreur lors de la génération du tableau d'amortissement", e);
        }
    }

    /**
     * Récupère le tableau d'amortissement d'un prêt
     */
    public List<AmortissementPret> getAmortissementByPretId(Integer pretId) {
        return persistenceService.getAmortissementByPretId(pretId);
    }

    /**
     * Supprime le tableau d'amortissement d'un prêt
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void supprimerAmortissement(Integer pretId) {
        persistenceService.supprimerAmortissement(pretId);
    }

    /**
     * Vérifie si un tableau d'amortissement existe pour un prêt
     */
    public boolean amortissementExists(Integer pretId) {
        return persistenceService.amortissementExists(pretId);
    }
}