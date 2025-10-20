package com.example.serveurcomptecourant.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.serveurcomptecourant.models.CompteCourant;
import com.example.serveurcomptecourant.repository.CompteCourantRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class CompteCourantService {
    private static final Logger LOGGER = Logger.getLogger(CompteCourantService.class.getName());
    
    @EJB
    private CompteCourantRepository repository;

    /**
     * Récupère tous les comptes
     */
    public List<CompteCourant> getAllComptes() {
        try {
            return repository.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de tous les comptes", e);
            throw new RuntimeException("Erreur lors de la récupération des comptes", e);
        }
    }

    /**
     * Récupère un compte par son ID
     */
    public CompteCourant getCompteById(Integer id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID du compte invalide");
            }
            
            CompteCourant compte = repository.find(id);
            if (compte == null) {
                throw new RuntimeException("Compte non trouvé avec l'ID: " + id);
            }
            
            return compte;
        } catch (RuntimeException e) {
            throw e; // Re-lancer les exceptions runtime
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du compte " + id, e);
            throw new RuntimeException("Erreur lors de la récupération du compte", e);
        }
    }

    /**
     * Crée un nouveau compte
     */
    public CompteCourant createCompte(CompteCourant compte) {
        try {
            // Validation des données
            validateCompteData(compte);
            
            repository.save(compte);
            return compte;
        } catch (RuntimeException e) {
            throw e; // Re-lancer les exceptions runtime
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création du compte", e);
            throw new RuntimeException("Erreur lors de la création du compte", e);
        }
    }

    /**
     * Met à jour un compte existant
     */
    public CompteCourant updateCompte(CompteCourant compte) {
        try {
            // Validation des données
            validateCompteData(compte);
            
            // Vérifier que le compte existe
            if (compte.getIdCompte() == null || repository.find(compte.getIdCompte()) == null) {
                throw new RuntimeException("Compte non trouvé avec l'ID: " + compte.getIdCompte());
            }
            
            repository.save(compte);
            return compte;
        } catch (RuntimeException e) {
            throw e; // Re-lancer les exceptions runtime
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du compte", e);
            throw new RuntimeException("Erreur lors de la mise à jour du compte", e);
        }
    }

    /**
     * Supprime un compte
     */
    public void deleteCompte(Integer id) {
        try {
            CompteCourant compte = getCompteById(id);
            repository.delete(compte);
        } catch (RuntimeException e) {
            throw e; // Re-lancer les exceptions runtime
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du compte " + id, e);
            throw new RuntimeException("Erreur lors de la suppression du compte", e);
        }
    }

    /**
     * Met à jour le solde d'un compte
     */
    public CompteCourant updateSolde(Integer idCompte, BigDecimal nouveauSolde) {
        try {
            CompteCourant compte = getCompteById(idCompte);
            compte.setSolde(nouveauSolde);
            repository.save(compte);
            return compte;
        } catch (RuntimeException e) {
            throw e; // Re-lancer les exceptions runtime
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du solde du compte " + idCompte, e);
            throw new RuntimeException("Erreur lors de la mise à jour du solde", e);
        }
    }

    /**
     * Validation des données du compte
     */
    private void validateCompteData(CompteCourant compte) {
        if (compte == null) {
            throw new IllegalArgumentException("Les données du compte sont obligatoires");
        }
        
        if (compte.getSolde() == null) {
            compte.setSolde(BigDecimal.ZERO);
        }
    }
}
