package com.example.comptecourant.ejb;

import com.example.comptecourant.exceptions.CompteCourantException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
     * Convertit une entité CompteCourant en DTO
     */
    private com.example.centralizer.dto.comptecourant.CompteCourant convertToDTO(com.example.comptecourant.models.CompteCourant entity) {
        if (entity == null) {
            return null;
        }
        return new com.example.centralizer.dto.comptecourant.CompteCourant(entity.getIdCompte(), entity.getSolde());
    }

    /**
     * Convertit un DTO CompteCourant en entité
     */
    private com.example.comptecourant.models.CompteCourant convertToEntity(com.example.centralizer.dto.comptecourant.CompteCourant dto) {
        if (dto == null) {
            return null;
        }
        com.example.comptecourant.models.CompteCourant entity = new com.example.comptecourant.models.CompteCourant();
        entity.setIdCompte(dto.getIdCompte());
        entity.setSolde(dto.getSolde());
        return entity;
    }

    /**
     * Récupère tous les comptes
     */
    @Override
    public List<com.example.centralizer.dto.comptecourant.CompteCourant> getAllComptes() throws CompteCourantException {
        try {
            List<com.example.comptecourant.models.CompteCourant> entities = em.createQuery("SELECT c FROM CompteCourant c", com.example.comptecourant.models.CompteCourant.class).getResultList();
            return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de tous les comptes", e);
            throw new CompteCourantException("Erreur lors de la récupération des comptes: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère un compte par son ID
     */
    @Override
    public com.example.centralizer.dto.comptecourant.CompteCourant getCompteById(Integer id) throws CompteCourantException {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID du compte invalide");
            }

            com.example.comptecourant.models.CompteCourant compte = em.find(com.example.comptecourant.models.CompteCourant.class, id);
            if (compte == null) {
                throw new CompteCourantException("Compte non trouvé avec l'ID: " + id);
            }

            return convertToDTO(compte);
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
    public com.example.centralizer.dto.comptecourant.CompteCourant createCompte(com.example.centralizer.dto.comptecourant.CompteCourant compte) throws CompteCourantException {
        try {
            com.example.comptecourant.models.CompteCourant entity = convertToEntity(compte);
            validateCompteData(entity);
            em.persist(entity);
            return convertToDTO(entity);
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
    public com.example.centralizer.dto.comptecourant.CompteCourant updateCompte(com.example.centralizer.dto.comptecourant.CompteCourant compte) throws CompteCourantException {
        try {
            com.example.comptecourant.models.CompteCourant entity = convertToEntity(compte);
            validateCompteData(entity);

            if (entity.getIdCompte() == null || em.find(com.example.comptecourant.models.CompteCourant.class, entity.getIdCompte()) == null) {
                throw new CompteCourantException("Compte non trouvé avec l'ID: " + entity.getIdCompte());
            }

            com.example.comptecourant.models.CompteCourant updated = em.merge(entity);
            return convertToDTO(updated);
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
            com.example.centralizer.dto.comptecourant.CompteCourant compteDTO = getCompteById(id);
            com.example.comptecourant.models.CompteCourant compte = convertToEntity(compteDTO);
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
    public com.example.centralizer.dto.comptecourant.CompteCourant updateSolde(Integer idCompte, BigDecimal nouveauSolde) throws CompteCourantException {
        try {
            com.example.centralizer.dto.comptecourant.CompteCourant compteDTO = getCompteById(idCompte);
            com.example.comptecourant.models.CompteCourant compte = convertToEntity(compteDTO);
            compte.setSolde(nouveauSolde);
            com.example.comptecourant.models.CompteCourant updated = em.merge(compte);
            return convertToDTO(updated);
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
    private void validateCompteData(com.example.comptecourant.models.CompteCourant compte) throws CompteCourantException {
        if (compte == null) {
            throw new CompteCourantException("Les données du compte sont obligatoires");
        }

        if (compte.getSolde() == null) {
            compte.setSolde(BigDecimal.ZERO);
        }
    }
}
