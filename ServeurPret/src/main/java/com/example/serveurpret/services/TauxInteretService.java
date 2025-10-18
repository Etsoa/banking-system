package com.example.serveurpret.services;

import com.example.serveurpret.models.*;
import com.example.serveurpret.repository.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Logger;

@Stateless
public class TauxInteretService {
    private static final Logger LOGGER = Logger.getLogger(TauxInteretService.class.getName());

    @EJB
    private TauxInteretRepository tauxInteretRepository;

    /**
     * Récupère le taux d'intérêt actuel
     */
    public TauxInteret getTauxActuel() {
        TauxInteret tauxActuel = tauxInteretRepository.findCurrentTaux();
        if (tauxActuel == null) {
            throw new RuntimeException("Aucun taux d'intérêt défini");
        }
        return tauxActuel;
    }

    /**
     * Calcule le taux périodique à partir du taux annuel
     */
    public BigDecimal calculerTauxPeriodique(BigDecimal tauxAnnuel, Integer nombreMoisParPeriode) {
        if (tauxAnnuel == null || nombreMoisParPeriode == null || nombreMoisParPeriode <= 0) {
            throw new IllegalArgumentException("Paramètres invalides pour le calcul du taux périodique");
        }
        
        BigDecimal tauxDecimal = tauxAnnuel.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
        BigDecimal tauxMensuel = tauxDecimal.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
        return tauxMensuel.multiply(new BigDecimal(nombreMoisParPeriode));
    }

    /**
     * Calcule l'annuité constante
     */
    public BigDecimal calculerAnnuite(BigDecimal capital, BigDecimal tauxPeriodique, Integer nombrePeriodes) {
        if (capital == null || tauxPeriodique == null || nombrePeriodes == null || nombrePeriodes <= 0) {
            throw new IllegalArgumentException("Paramètres invalides pour le calcul de l'annuité");
        }
        
        if (tauxPeriodique.compareTo(BigDecimal.ZERO) == 0) {
            return capital.divide(new BigDecimal(nombrePeriodes), 2, RoundingMode.HALF_UP);
        }
        
        BigDecimal unPlusTaux = BigDecimal.ONE.add(tauxPeriodique);
        BigDecimal puissance = unPlusTaux.pow(nombrePeriodes);
        
        BigDecimal numerateur = capital.multiply(tauxPeriodique).multiply(puissance);
        BigDecimal denominateur = puissance.subtract(BigDecimal.ONE);
        
        return numerateur.divide(denominateur, 2, RoundingMode.HALF_UP);
    }

    /**
     * Calcule l'amortissement constant
     */
    public BigDecimal calculerAmortissementConstant(BigDecimal capital, Integer nombrePeriodes) {
        if (capital == null || nombrePeriodes == null || nombrePeriodes <= 0) {
            throw new IllegalArgumentException("Paramètres invalides pour le calcul de l'amortissement constant");
        }
        
        return capital.divide(new BigDecimal(nombrePeriodes), 2, RoundingMode.HALF_UP);
    }
}