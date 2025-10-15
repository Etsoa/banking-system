package com.example.serveurcomptecourant.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.serveurcomptecourant.exceptions.CompteCourantBusinessException;
import com.example.serveurcomptecourant.exceptions.CompteCourantException;
import com.example.serveurcomptecourant.models.CompteCourant;
import com.example.serveurcomptecourant.repository.CompteCourantRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class CompteCourantService {
    private static final Logger LOGGER = Logger.getLogger(CompteCourantService.class.getName());
    
    @EJB
    private CompteCourantRepository repository;

    public List<CompteCourant> getAllComptes() throws CompteCourantException {
        try {
            return repository.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de tous les comptes", e);
            throw new CompteCourantException("Erreur lors de la récupération des comptes", e);
        }
    }

    public List<CompteCourant> getComptesByClientId(Long clientId) throws CompteCourantException {
        try {
            if (clientId == null || clientId <= 0) {
                throw new CompteCourantBusinessException.ClientInexistantException(clientId);
            }
            return repository.findByClientId(clientId);
        } catch (CompteCourantBusinessException e) {
            throw e; // Re-lancer les exceptions métier
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des comptes pour le client " + clientId, e);
            throw new CompteCourantException("Erreur lors de la récupération des comptes du client", e);
        }
    }

    public CompteCourant getCompteById(Long id) throws CompteCourantException {
        try {
            if (id == null || id <= 0) {
                throw new CompteCourantBusinessException.CompteNotFoundException(id);
            }
            
            CompteCourant compte = repository.find(id);
            if (compte == null) {
                throw new CompteCourantBusinessException.CompteNotFoundException(id);
            }
            
            return compte;
        } catch (CompteCourantBusinessException e) {
            throw e; // Re-lancer les exceptions métier
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du compte " + id, e);
            throw new CompteCourantException("Erreur lors de la récupération du compte", e);
        }
    }

    public CompteCourant createCompte(CompteCourant compte) throws CompteCourantException {
        try {
            // Validation des données
            validateCompteData(compte);
            
            // Vérifier l'existence du client (utiliser idClient, pas clientId)
            if (compte.getIdClient() == null || compte.getIdClient() <= 0) {
                throw new CompteCourantBusinessException.ClientInexistantException(compte.getIdClient().longValue());
            }
            
            repository.save(compte);
            return compte;
        } catch (CompteCourantBusinessException e) {
            throw e; // Re-lancer les exceptions métier
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création du compte", e);
            throw new CompteCourantException("Erreur lors de la création du compte", e);
        }
    }

    /**
     * Validation des données du compte
     */
    private void validateCompteData(CompteCourant compte) throws CompteCourantBusinessException {
        if (compte == null) {
            throw new CompteCourantBusinessException("Les données du compte sont obligatoires");
        }
        
        if (compte.getSolde() != null && compte.getSolde().compareTo(BigDecimal.ZERO) < 0) {
            throw new CompteCourantBusinessException.MontantInvalideException(compte.getSolde().doubleValue());
        }
        
        if (compte.getIdClient() == null) {
            throw new CompteCourantBusinessException("L'ID du client est obligatoire");
        }
    }
}
