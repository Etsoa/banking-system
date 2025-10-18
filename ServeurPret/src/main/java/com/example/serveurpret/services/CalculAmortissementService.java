package com.example.serveurpret.services;

import com.example.serveurpret.models.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class CalculAmortissementService {
    private static final Logger LOGGER = Logger.getLogger(CalculAmortissementService.class.getName());

    @EJB
    private TauxInteretService tauxInteretService;

    /**
     * Génère un tableau d'amortissement à annuité constante
     */
    public List<AmortissementPret> genererAnnuiteConstante(Pret pret, BigDecimal tauxPeriodique, Modalite modalite) {
        List<AmortissementPret> amortissements = new ArrayList<>();
        
        BigDecimal capital = pret.getMontant();
        Integer nombrePeriodes = pret.getDureePeriode();
        
        // Calcul de l'annuité constante
        BigDecimal annuite = tauxInteretService.calculerAnnuite(capital, tauxPeriodique, nombrePeriodes);
        
        BigDecimal capitalRestant = capital;
        LocalDate dateEcheance = pret.getDateDebut();

        for (int periode = 1; periode <= nombrePeriodes; periode++) {
            // Calcul des intérêts de la période
            BigDecimal interets = capitalRestant.multiply(tauxPeriodique).setScale(2, RoundingMode.HALF_UP);
            
            // Calcul de l'amortissement de la période
            BigDecimal amortissement = annuite.subtract(interets).setScale(2, RoundingMode.HALF_UP);
            
            // Ajustement pour la dernière période
            if (periode == nombrePeriodes) {
                amortissement = capitalRestant;
                annuite = amortissement.add(interets);
            }
            
            // Capital restant après cette période
            BigDecimal nouveauCapitalRestant = capitalRestant.subtract(amortissement);
            
            // Calcul de la date d'échéance
            dateEcheance = dateEcheance.plusMonths(modalite.getNombreMois());

            AmortissementPret ligne = createAmortissementPret(
                pret.getId(), periode, dateEcheance, 
                capitalRestant, interets, amortissement, annuite, nouveauCapitalRestant
            );

            amortissements.add(ligne);
            
            capitalRestant = nouveauCapitalRestant;
        }

        LOGGER.info("Tableau d'amortissement à annuité constante généré : " + amortissements.size() + " périodes");
        return amortissements;
    }

    /**
     * Génère un tableau d'amortissement à amortissement constant
     */
    public List<AmortissementPret> genererAmortissementConstant(Pret pret, BigDecimal tauxPeriodique, Modalite modalite) {
        List<AmortissementPret> amortissements = new ArrayList<>();
        
        BigDecimal capital = pret.getMontant();
        Integer nombrePeriodes = pret.getDureePeriode();
        
        // Calcul de l'amortissement constant
        BigDecimal amortissementConstant = tauxInteretService.calculerAmortissementConstant(capital, nombrePeriodes);
        
        BigDecimal capitalRestant = capital;
        LocalDate dateEcheance = pret.getDateDebut();

        for (int periode = 1; periode <= nombrePeriodes; periode++) {
            // Calcul des intérêts de la période
            BigDecimal interets = capitalRestant.multiply(tauxPeriodique).setScale(2, RoundingMode.HALF_UP);
            
            BigDecimal amortissement = amortissementConstant;
            
            // Ajustement pour la dernière période
            if (periode == nombrePeriodes) {
                amortissement = capitalRestant;
            }
            
            // Calcul de l'annuité
            BigDecimal annuite = amortissement.add(interets);
            
            // Capital restant après cette période
            BigDecimal nouveauCapitalRestant = capitalRestant.subtract(amortissement);
            
            // Calcul de la date d'échéance
            dateEcheance = dateEcheance.plusMonths(modalite.getNombreMois());

            AmortissementPret ligne = createAmortissementPret(
                pret.getId(), periode, dateEcheance, 
                capitalRestant, interets, amortissement, annuite, nouveauCapitalRestant
            );

            amortissements.add(ligne);
            
            capitalRestant = nouveauCapitalRestant;
        }

        LOGGER.info("Tableau d'amortissement à amortissement constant généré : " + amortissements.size() + " périodes");
        return amortissements;
    }

    /**
     * Crée une ligne d'amortissement
     */
    private AmortissementPret createAmortissementPret(Integer idPret, Integer periode, LocalDate dateEcheance,
                                                     BigDecimal capitalDebut, BigDecimal interets, 
                                                     BigDecimal amortissement, BigDecimal annuite,
                                                     BigDecimal nouveauCapitalRestant) {
        AmortissementPret ligne = new AmortissementPret();
        ligne.setIdPret(idPret);
        ligne.setPeriode(periode);
        ligne.setDateEcheance(dateEcheance);
        ligne.setCapitalDebut(capitalDebut);
        ligne.setInterets(interets);
        ligne.setAmortissement(amortissement);
        ligne.setAnnuite(annuite);
        ligne.setCapitalRestant(nouveauCapitalRestant.max(BigDecimal.ZERO));

        return ligne;
    }
}