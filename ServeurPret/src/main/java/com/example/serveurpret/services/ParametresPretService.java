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
}