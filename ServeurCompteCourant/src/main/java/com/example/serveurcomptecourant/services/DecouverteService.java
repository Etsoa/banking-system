package com.example.serveurcomptecourant.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.serveurcomptecourant.exceptions.CompteCourantBusinessException;
import com.example.serveurcomptecourant.exceptions.CompteCourantException;
import com.example.serveurcomptecourant.models.Decouverte;
import com.example.serveurcomptecourant.repository.DecouverteRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class DecouverteService {
    private static final Logger LOGGER = Logger.getLogger(DecouverteService.class.getName());
    
    @EJB
    private DecouverteRepository decouverteRepository;

    /**
     * Récupère toutes les découvertes
     */
    public List<Decouverte> getAllDecouvertes() throws CompteCourantException {
        try {
            return decouverteRepository.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de toutes les découvertes", e);
            throw new CompteCourantException("Erreur lors de la récupération des découvertes", e);
        }
    }

    /**
     * Récupère une découverte par ID
     */
    public Decouverte getDecouverteById(Integer id) throws CompteCourantException {
        try {
            if (id == null || id <= 0) {
                throw new CompteCourantBusinessException("L'ID de la découverte est obligatoire");
            }
            
            Decouverte decouverte = decouverteRepository.find(id);
            if (decouverte == null) {
                throw new CompteCourantBusinessException("Découverte introuvable");
            }
            
            return decouverte;
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de la découverte " + id, e);
            throw new CompteCourantException("Erreur lors de la récupération de la découverte", e);
        }
    }

    /**
     * Trouve la découverte actuelle basée sur le revenu et la date de référence
     */
    public Decouverte findCurrentDecouverte(BigDecimal revenu, LocalDateTime dateReference) throws CompteCourantException {
        try {
            if (revenu == null || revenu.compareTo(BigDecimal.ZERO) < 0) {
                throw new CompteCourantBusinessException("Le revenu doit être spécifié et positif");
            }
            
            if (dateReference == null) {
                throw new CompteCourantBusinessException("La date de référence est obligatoire");
            }
            
            return decouverteRepository.findCurrentDecouverte(revenu, dateReference);
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de la découverte actuelle pour revenu " + revenu, e);
            throw new CompteCourantException("Erreur lors de la recherche de la découverte actuelle", e);
        }
    }

    /**
     * Trouve les découvertes par plage de revenus
     */
    public List<Decouverte> findByRevenuRange(BigDecimal revenuMin, BigDecimal revenuMax) throws CompteCourantException {
        try {
            if (revenuMin == null || revenuMax == null) {
                throw new CompteCourantBusinessException("Les revenus minimum et maximum sont obligatoires");
            }
            
            if (revenuMin.compareTo(revenuMax) > 0) {
                throw new CompteCourantBusinessException("Le revenu minimum ne peut pas être supérieur au revenu maximum");
            }
            
            return decouverteRepository.findByRevenuRange(revenuMin, revenuMax);
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche des découvertes par plage de revenus", e);
            throw new CompteCourantException("Erreur lors de la recherche des découvertes", e);
        }
    }

    /**
     * Crée une nouvelle découverte
     */
    public Decouverte createDecouverte(Decouverte decouverte) throws CompteCourantException {
        try {
            validateDecouverteData(decouverte);
            
            decouverteRepository.save(decouverte);
            return decouverte;
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de la découverte", e);
            throw new CompteCourantException("Erreur lors de la création de la découverte", e);
        }
    }

    /**
     * Met à jour une découverte existante
     */
    public Decouverte updateDecouverte(Decouverte decouverte) throws CompteCourantException {
        try {
            validateDecouverteData(decouverte);
            
            if (decouverte.getId() == null) {
                throw new CompteCourantBusinessException("L'ID de la découverte est obligatoire pour la mise à jour");
            }
            
            Decouverte existing = decouverteRepository.find(decouverte.getId());
            if (existing == null) {
                throw new CompteCourantBusinessException("Découverte introuvable");
            }
            
            decouverteRepository.save(decouverte);
            return decouverte;
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de la découverte", e);
            throw new CompteCourantException("Erreur lors de la mise à jour de la découverte", e);
        }
    }

    /**
     * Supprime une découverte
     */
    public void deleteDecouverte(Integer id) throws CompteCourantException {
        try {
            if (id == null || id <= 0) {
                throw new CompteCourantBusinessException("L'ID de la découverte est obligatoire");
            }
            
            Decouverte existing = decouverteRepository.find(id);
            if (existing == null) {
                throw new CompteCourantBusinessException("Découverte introuvable");
            }
            
            decouverteRepository.delete(id);
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de la découverte " + id, e);
            throw new CompteCourantException("Erreur lors de la suppression de la découverte", e);
        }
    }

    /**
     * Validation des données de découverte
     */
    private void validateDecouverteData(Decouverte decouverte) throws CompteCourantBusinessException {
        if (decouverte == null) {
            throw new CompteCourantBusinessException("Les données de la découverte sont obligatoires");
        }
        
        if (decouverte.getDateDebut() == null) {
            throw new CompteCourantBusinessException("La date de début est obligatoire");
        }
        
        if (decouverte.getRevenuMin() == null || decouverte.getRevenuMin().compareTo(BigDecimal.ZERO) < 0) {
            throw new CompteCourantBusinessException("Le revenu minimum doit être spécifié et positif");
        }
        
        if (decouverte.getRevenuMax() == null || decouverte.getRevenuMax().compareTo(BigDecimal.ZERO) < 0) {
            throw new CompteCourantBusinessException("Le revenu maximum doit être spécifié et positif");
        }
        
        if (decouverte.getRevenuMin().compareTo(decouverte.getRevenuMax()) > 0) {
            throw new CompteCourantBusinessException("Le revenu minimum ne peut pas être supérieur au revenu maximum");
        }
        
        if (decouverte.getValeur() == null || decouverte.getValeur() <= 0) {
            throw new CompteCourantBusinessException("La valeur de découvert doit être spécifiée et positive");
        }
    }
}