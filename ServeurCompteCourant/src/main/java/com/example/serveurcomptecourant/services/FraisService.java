package com.example.serveurcomptecourant.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.serveurcomptecourant.exceptions.CompteCourantBusinessException;
import com.example.serveurcomptecourant.exceptions.CompteCourantException;
import com.example.serveurcomptecourant.models.Frais;
import com.example.serveurcomptecourant.repository.FraisRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class FraisService {
    private static final Logger LOGGER = Logger.getLogger(FraisService.class.getName());
    
    @EJB
    private FraisRepository fraisRepository;

    /**
     * Récupère tous les frais
     */
    public List<Frais> getAllFrais() throws CompteCourantException {
        try {
            return fraisRepository.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de tous les frais", e);
            throw new CompteCourantException("Erreur lors de la récupération des frais", e);
        }
    }

    /**
     * Récupère un frais par ID
     */
    public Frais getFraisById(Integer id) throws CompteCourantException {
        try {
            if (id == null || id <= 0) {
                throw new CompteCourantBusinessException("L'ID du frais est obligatoire");
            }
            
            Frais frais = fraisRepository.find(id);
            if (frais == null) {
                throw new CompteCourantBusinessException("Frais introuvable");
            }
            
            return frais;
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du frais " + id, e);
            throw new CompteCourantException("Erreur lors de la récupération du frais", e);
        }
    }

    /**
     * Trouve les frais actuels pour un type de transaction et un montant donnés
     */
    public Frais findCurrentFrais(String typeTransaction, BigDecimal montant, LocalDateTime dateReference) throws CompteCourantException {
        try {
            if (typeTransaction == null || typeTransaction.trim().isEmpty()) {
                throw new CompteCourantBusinessException("Le type de transaction est obligatoire");
            }
            
            if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
                throw new CompteCourantBusinessException("Le montant doit être spécifié et positif");
            }
            
            if (dateReference == null) {
                throw new CompteCourantBusinessException("La date de référence est obligatoire");
            }
            
            // Déterminer le nom des frais selon le type de transaction
            String nomFrais;
            if ("Retrait".equalsIgnoreCase(typeTransaction)) {
                nomFrais = "Frais de retrait";
            } else if ("Virement sortant".equalsIgnoreCase(typeTransaction)) {
                nomFrais = "Frais de virement sortant";
            } else {
                // Pour les autres types de transaction, pas de frais
                return null;
            }
            
            return fraisRepository.findCurrentFrais(nomFrais, montant, dateReference);
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche des frais actuels pour " + typeTransaction, e);
            throw new CompteCourantException("Erreur lors de la recherche des frais actuels", e);
        }
    }


    /**
     * Validation des données de frais
     */
    private void validateFraisData(Frais frais) throws CompteCourantBusinessException {
        if (frais == null) {
            throw new CompteCourantBusinessException("Les données du frais sont obligatoires");
        }
        
        if (frais.getDateDebut() == null) {
            throw new CompteCourantBusinessException("La date de début est obligatoire");
        }
        
        if (frais.getNom() == null || frais.getNom().trim().isEmpty()) {
            throw new CompteCourantBusinessException("Le nom du frais est obligatoire");
        }
        
        if (frais.getMontantMin() == null || frais.getMontantMin().compareTo(BigDecimal.ZERO) < 0) {
            throw new CompteCourantBusinessException("Le montant minimum doit être spécifié et positif ou nul");
        }
        
        if (frais.getMontantMax() == null || frais.getMontantMax().compareTo(BigDecimal.ZERO) < 0) {
            throw new CompteCourantBusinessException("Le montant maximum doit être spécifié et positif ou nul");
        }
        
        if (frais.getMontantMin().compareTo(frais.getMontantMax()) > 0) {
            throw new CompteCourantBusinessException("Le montant minimum ne peut pas être supérieur au montant maximum");
        }
        
        if (frais.getValeur() == null || frais.getValeur() < 0) {
            throw new CompteCourantBusinessException("Le montant des frais doit être spécifié et positif ou nul");
        }
    }
}