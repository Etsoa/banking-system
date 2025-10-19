package com.example.serveurpret.services;

import com.example.serveurpret.models.*;
import com.example.serveurpret.repository.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.math.BigDecimal;
import java.util.logging.Logger;

@Stateless
public class ValidationPretService {
    private static final Logger LOGGER = Logger.getLogger(ValidationPretService.class.getName());

    @EJB
    private PlageDureePretRepository plageRepository;

    @EJB
    private ModaliteRepository modaliteRepository;

    @EJB
    private TypeRemboursementRepository typeRemboursementRepository;

    /**
     * Valide la durée du prêt selon les plages définies
     */
    public void validerDureePret(BigDecimal montant, Integer dureeMois) {
        if (montant == null || dureeMois == null) {
            throw new IllegalArgumentException("Le montant et la durée ne peuvent pas être null");
        }

        PlageDureePret plage = plageRepository.findByMontant(montant);
        if (plage == null) {
            throw new RuntimeException("Aucune plage de durée trouvée pour ce montant");
        }

        if (dureeMois < plage.getDureeMinMois() || dureeMois > plage.getDureeMaxMois()) {
            throw new RuntimeException(
                String.format("La durée doit être comprise entre %d et %d mois pour ce montant", 
                    plage.getDureeMinMois(), plage.getDureeMaxMois())
            );
        }

        LOGGER.info("Durée validée : " + dureeMois + " mois pour montant " + montant);
    }

    /**
     * Calcule la durée en périodes
     */
    public Integer calculerDureePeriode(Integer dureeMois, Integer modaliteId) {
        if (dureeMois == null || modaliteId == null) {
            throw new IllegalArgumentException("La durée et la modalité ne peuvent pas être null");
        }

        Modalite modalite = getModalite(modaliteId);

        // Utilisation de Math.floor pour arrondir au inférieur
        double periodes = Math.floor((double) dureeMois / modalite.getNombreMois());
        return (int) periodes;
    }

    /**
     * Récupère et valide la modalité de remboursement
     */
    public Modalite getModalite(Integer modaliteId) {
        if (modaliteId == null) {
            throw new IllegalArgumentException("L'ID de la modalité ne peut pas être null");
        }

        Modalite modalite = modaliteRepository.findById(modaliteId);
        if (modalite == null) {
            throw new RuntimeException("Modalité de remboursement non trouvée");
        }

        return modalite;
    }

    /**
     * Récupère et valide le type de remboursement
     */
    public TypeRemboursement getTypeRemboursement(Integer typeRemboursementId) {
        if (typeRemboursementId == null) {
            throw new IllegalArgumentException("L'ID du type de remboursement ne peut pas être null");
        }

        TypeRemboursement type = typeRemboursementRepository.findById(typeRemboursementId);
        if (type == null) {
            throw new RuntimeException("Type de remboursement non trouvé");
        }

        return type;
    }

    /**
     * Valide tous les paramètres d'un prêt
     */
    public void validerParametresPret(BigDecimal montant, Integer dureeMois, Integer modaliteId, Integer typeRemboursementId) {
        // Validation des paramètres de base
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }

        if (dureeMois == null || dureeMois <= 0) {
            throw new IllegalArgumentException("La durée doit être positive");
        }

        // Validation de la durée selon les plages
        validerDureePret(montant, dureeMois);

        // Validation des références
        getModalite(modaliteId);
        getTypeRemboursement(typeRemboursementId);

        LOGGER.info("Tous les paramètres du prêt sont valides");
    }
}