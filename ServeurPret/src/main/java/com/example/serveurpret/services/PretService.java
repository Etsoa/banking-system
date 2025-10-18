package com.example.serveurpret.services;

import com.example.serveurpret.models.*;
import com.example.serveurpret.repository.PretRepository;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class PretService {
    private static final Logger LOGGER = Logger.getLogger(PretService.class.getName());

    @EJB
    private PretRepository pretRepository;

    @EJB
    private ValidationPretService validationService;

    @EJB
    private AmortissementService amortissementService;

    public List<Pret> getAllPrets() {
        return pretRepository.findAll();
    }

    public List<Pret> getPretsByClientId(String clientId) {
        return pretRepository.findByClientId(clientId);
    }

    public Pret getPretById(Integer id) {
        return pretRepository.findById(id);
    }

    /**
     * Crée un prêt complet avec validation et génération du tableau d'amortissement
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Pret createPret(String clientId, BigDecimal montant, Integer dureeMois, 
                          Integer modaliteId, Integer typeRemboursementId) {
        try {
            LOGGER.info("Création d'un prêt pour client " + clientId + ", montant: " + montant + ", durée: " + dureeMois + " mois");
            
            // 1. Validation complète des paramètres
            validationService.validerParametresPret(montant, dureeMois, modaliteId, typeRemboursementId);

            // 2. Récupération des entités de référence
            Modalite modalite = validationService.getModalite(modaliteId);
            TypeRemboursement typeRemboursement = validationService.getTypeRemboursement(typeRemboursementId);

            // 3. Calcul de la durée en périodes
            Integer dureePeriode = validationService.calculerDureePeriode(dureeMois, modaliteId);

            // 4. Création du prêt
            Pret pret = new Pret();
            pret.setIdClient(clientId);
            pret.setMontant(montant);
            pret.setDureeMois(dureeMois);
            pret.setDureePeriode(dureePeriode);
            pret.setIdModalite(modaliteId);
            pret.setIdTypeRemboursement(typeRemboursementId);
            pret.setDateDebut(LocalDate.now());
            pret.setIdStatutPret(1); // Statut "EN_COURS" par défaut

            // 5. Sauvegarde du prêt
            pretRepository.save(pret);

            // 6. Génération du tableau d'amortissement
            amortissementService.genererAmortissement(pret, typeRemboursement, modalite);

            LOGGER.info("Prêt créé avec succès - ID: " + pret.getId());
            return pret;

        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la création du prêt : " + e.getMessage());
            throw new RuntimeException("Erreur lors de la création du prêt: " + e.getMessage(), e);
        }
    }

    /**
     * Version simple pour compatibilité
     */
    public void createPret(Pret pret) {
        pretRepository.save(pret);
    }

    public Pret updatePret(Pret pret) {
        return pretRepository.update(pret);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deletePret(Pret pret) {
        try {
            // Supprimer d'abord le tableau d'amortissement
            if (amortissementService.amortissementExists(pret.getId())) {
                amortissementService.supprimerAmortissement(pret.getId());
            }
            
            // Puis supprimer le prêt
            pretRepository.delete(pret);
            
            LOGGER.info("Prêt supprimé avec succès - ID: " + pret.getId());
            
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la suppression du prêt : " + e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression du prêt", e);
        }
    }

    /**
     * Récupère le tableau d'amortissement d'un prêt
     */
    public List<AmortissementPret> getAmortissementPret(Integer pretId) {
        return amortissementService.getAmortissementByPretId(pretId);
    }
}
