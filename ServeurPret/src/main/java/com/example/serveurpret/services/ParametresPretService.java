package com.example.serveurpret.services;

import com.example.serveurpret.models.*;
import com.example.serveurpret.repository.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.math.BigDecimal;
import java.util.List;


@Stateless
public class ParametresPretService {

    @EJB
    private ModaliteRepository modaliteRepository;

    @EJB
    private TypeRemboursementRepository typeRemboursementRepository;

    @EJB
    private PlageDureePretRepository plageRepository;

    @EJB
    private TauxInteretRepository tauxInteretRepository;

    @EJB
    private MethodeRemboursementRepository methodeRemboursementRepository;

    @EJB
    private PlafondPretRevenuRepository plafondPretRevenuRepository;

    /**
     * Récupère toutes les modalités de remboursement disponibles
     */
    public List<Modalite> getAllModalites() {
        return modaliteRepository.findAll();
    }

    /**
     * Récupère tous les types de remboursement disponibles
     */
    public List<TypeRemboursement> getAllTypesRemboursement() {
        return typeRemboursementRepository.findAll();
    }

    /**
     * Récupère les plages de durée selon le montant
     */
    public PlageDureePret getPlageDureeByMontant(BigDecimal montant) {
        return plageRepository.findByMontant(montant);
    }

    /**
     * Récupère le taux d'intérêt actuel
     */
    public TauxInteret getTauxActuel() {
        return tauxInteretRepository.findCurrentTaux();
    }

    /**
     * Récupère une modalité par son ID
     */
    public Modalite getModaliteById(Integer id) {
        return modaliteRepository.findById(id);
    }

    /**
     * Récupère un type de remboursement par son ID
     */
    public TypeRemboursement getTypeRemboursementById(Integer id) {
        return typeRemboursementRepository.findById(id);
    }

    /**
     * Récupère toutes les méthodes de remboursement disponibles
     */
    public List<MethodeRemboursement> getMethodesRemboursement() {
        return methodeRemboursementRepository.findAll();
    }

    /**
     * Valide si le montant demandé respecte le plafond selon le revenu du client
     */
    public PlafondPretRevenu validatePlafondPret(BigDecimal revenu, BigDecimal montantDemande) {
        PlafondPretRevenu plafond = plafondPretRevenuRepository.findCurrentByRevenu(revenu);
        return plafond;
    }

    /**
     * Vérifie si un montant de prêt est autorisé pour un revenu donné
     */
    public boolean isPretAutorise(BigDecimal revenu, BigDecimal montantDemande) {
        PlafondPretRevenu plafond = plafondPretRevenuRepository.findCurrentByRevenu(revenu);
        if (plafond == null) {
            return false; // Aucun plafond trouvé, refus par défaut
        }
        return montantDemande.compareTo(plafond.getMontantMaxPret()) <= 0;
    }

    /**
     * Récupère le montant maximum autorisé pour un revenu donné
     */
    public BigDecimal getMontantMaxAutorise(BigDecimal revenu) {
        PlafondPretRevenu plafond = plafondPretRevenuRepository.findCurrentByRevenu(revenu);
        return plafond != null ? plafond.getMontantMaxPret() : BigDecimal.ZERO;
    }

    /**
     * Récupère tous les plafonds de prêt actuels
     */
    public List<PlafondPretRevenu> getAllPlafonds() {
        return plafondPretRevenuRepository.findCurrent();
    }
}