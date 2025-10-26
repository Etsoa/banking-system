package com.example.comptecourant.ejb;

import com.example.comptecourant.models.CompteCourant;
import com.example.comptecourant.exceptions.CompteCourantException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bean Stateless pour la gestion des comptes courants.
 * Implémente l'interface distante (Remote).
 * Logique métier basée sur le CompteCourantService original.
 */
@Stateless
public class CompteCourantServiceBean implements CompteCourantServiceRemote {

    private static final Logger LOGGER = Logger.getLogger(CompteCourantServiceBean.class.getName());

    @PersistenceContext(unitName = "CompteCourantPU")
    private EntityManager em;

    /**
     * Récupère tous les comptes
     */
    @Override
    public List<CompteCourant> getAllComptes() throws CompteCourantException {
        try {
            return em.createQuery("SELECT c FROM CompteCourant c", CompteCourant.class).getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de tous les comptes", e);
            throw new CompteCourantException("Erreur lors de la récupération des comptes: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère un compte par son ID
     */
    @Override
    public CompteCourant getCompteById(Integer id) throws CompteCourantException {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID du compte invalide");
            }

            CompteCourant compte = em.find(CompteCourant.class, id);
            if (compte == null) {
                throw new CompteCourantException("Compte non trouvé avec l'ID: " + id);
            }

            return compte;
        } catch (CompteCourantException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du compte " + id, e);
            throw new CompteCourantException("Erreur lors de la récupération du compte: " + e.getMessage(), e);
        }
    }

    /**
     * Crée un nouveau compte
     */
    @Override
    public CompteCourant createCompte(CompteCourant compte) throws CompteCourantException {
        try {
            validateCompteData(compte);
            em.persist(compte);
            return compte;
        } catch (CompteCourantException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création du compte", e);
            throw new CompteCourantException("Erreur lors de la création du compte: " + e.getMessage(), e);
        }
    }

    /**
     * Met à jour un compte existant
     */
    @Override
    public CompteCourant updateCompte(CompteCourant compte) throws CompteCourantException {
        try {
            validateCompteData(compte);

            if (compte.getIdCompte() == null || em.find(CompteCourant.class, compte.getIdCompte()) == null) {
                throw new CompteCourantException("Compte non trouvé avec l'ID: " + compte.getIdCompte());
            }

            return em.merge(compte);
        } catch (CompteCourantException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du compte", e);
            throw new CompteCourantException("Erreur lors de la mise à jour du compte: " + e.getMessage(), e);
        }
    }

    /**
     * Supprime un compte
     */
    @Override
    public void deleteCompte(Integer id) throws CompteCourantException {
        try {
            CompteCourant compte = getCompteById(id);
            em.remove(em.contains(compte) ? compte : em.merge(compte));
        } catch (CompteCourantException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du compte " + id, e);
            throw new CompteCourantException("Erreur lors de la suppression du compte: " + e.getMessage(), e);
        }
    }

    /**
     * Met à jour le solde d'un compte
     */
    @Override
    public CompteCourant updateSolde(Integer idCompte, BigDecimal nouveauSolde) throws CompteCourantException {
        try {
            CompteCourant compte = getCompteById(idCompte);
            compte.setSolde(nouveauSolde);
            return em.merge(compte);
        } catch (CompteCourantException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du solde du compte " + idCompte, e);
            throw new CompteCourantException("Erreur lors de la mise à jour du solde: " + e.getMessage(), e);
        }
    }

    /**
     * Validation des données du compte
     */
    private void validateCompteData(CompteCourant compte) throws CompteCourantException {
        if (compte == null) {
            throw new CompteCourantException("Les données du compte sont obligatoires");
        }

        if (compte.getSolde() == null) {
            compte.setSolde(BigDecimal.ZERO);
        }
    }
}
