package com.example.serveurpret.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

import com.example.serveurpret.models.AmortissementPret;
import com.example.serveurpret.models.Pret;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;

@Stateless
public class PretBusinessService {
    private static final Logger LOGGER = Logger.getLogger(PretBusinessService.class.getName());

    @EJB
    private PretService pretService;

    @EJB
    private ValidationPretService validationService;

    @EJB
    private AmortissementService amortissementService;

    /**
     * Point d'entrée principal pour la création d'un prêt
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Pret creerPretComplet(Integer clientId, BigDecimal montant, Integer dureeMois, 
                                Integer modaliteId, Integer typeRemboursementId) {
        
        LOGGER.info("Demande de création de prêt - Client: " + clientId + 
                   ", Montant: " + montant + ", Durée: " + dureeMois + " mois");
        
        try {
            // Utilisation du service métier PretService qui orchestre toute la logique
            return pretService.createPret(clientId, montant, dureeMois, modaliteId, typeRemboursementId);
            
        } catch (Exception e) {
            LOGGER.severe("Échec de la création du prêt : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Récupère un prêt avec son tableau d'amortissement
     */
    public PretAvecAmortissement getPretComplet(Integer pretId) {
        if (pretId == null) {
            throw new IllegalArgumentException("L'ID du prêt ne peut pas être null");
        }

        Pret pret = pretService.getPretById(pretId);
        if (pret == null) {
            throw new RuntimeException("Prêt non trouvé avec l'ID : " + pretId);
        }

        List<AmortissementPret> amortissements = pretService.getAmortissementPret(pretId);
        
        return new PretAvecAmortissement(pret, amortissements);
    }

    /**
     * Récupère tous les prêts d'un client avec leurs amortissements
     */
    public List<Pret> getPretsClient(Integer clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("L'ID du client ne peut pas être null");
        }

        return pretService.getPretsByClientId(clientId);
    }

    /**
     * Vérifie l'éligibilité d'un client pour un prêt
     */
    public EligibilitePret verifierEligibilite(Integer clientId, BigDecimal montant, Integer dureeMois) {
        try {
            // Validation des paramètres de base
            if (clientId == null) {
                return new EligibilitePret(false, "L'ID du client ne peut pas être null");
            }

            if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
                return new EligibilitePret(false, "Le montant doit être positif");
            }

            if (dureeMois == null || dureeMois <= 0) {
                return new EligibilitePret(false, "La durée doit être positive");
            }

            // Vérification des contraintes de durée selon le montant
            try {
                validationService.validerDureePret(montant, dureeMois);
            } catch (Exception validationException) {
                return new EligibilitePret(false, validationException.getMessage());
            }

            // Ici on pourrait ajouter d'autres vérifications :
            // - Vérification du nombre de prêts en cours
            // - Vérification de la capacité d'endettement
            // - Vérification du score de crédit, etc.

            return new EligibilitePret(true, "Client éligible pour ce prêt");

        } catch (Exception e) {
            return new EligibilitePret(false, e.getMessage());
        }
    }

    /**
     * Classe interne pour le résultat d'éligibilité
     */
    public static class EligibilitePret {
        private final boolean eligible;
        private final String message;

        public EligibilitePret(boolean eligible, String message) {
            this.eligible = eligible;
            this.message = message;
        }

        public boolean isEligible() {
            return eligible;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * Classe interne pour un prêt avec son amortissement
     */
    public static class PretAvecAmortissement {
        private final Pret pret;
        private final List<AmortissementPret> amortissements;

        public PretAvecAmortissement(Pret pret, List<AmortissementPret> amortissements) {
            this.pret = pret;
            this.amortissements = amortissements;
        }

        public Pret getPret() {
            return pret;
        }

        public List<AmortissementPret> getAmortissements() {
            return amortissements;
        }
    }
}