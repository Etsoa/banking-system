package com.example.serveurpret.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.serveurpret.models.AmortissementPret;
import com.example.serveurpret.models.Frais;
import com.example.serveurpret.models.MethodeRemboursement;
import com.example.serveurpret.models.Pret;
import com.example.serveurpret.models.Remboursement;
import com.example.serveurpret.models.StatutPret;
import com.example.serveurpret.models.StatutRemboursement;
import com.example.serveurpret.repository.AmortissementPretRepository;
import com.example.serveurpret.repository.FraisRepository;
import com.example.serveurpret.repository.MethodeRemboursementRepository;
import com.example.serveurpret.repository.PretRepository;
import com.example.serveurpret.repository.RemboursementRepository;
import com.example.serveurpret.repository.StatutPretRepository;
import com.example.serveurpret.repository.StatutRemboursementRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;

@Stateless
public class RemboursementService {
    private static final Logger LOGGER = Logger.getLogger(RemboursementService.class.getName());

    @EJB
    private RemboursementRepository remboursementRepository;

    @EJB
    private AmortissementPretRepository amortissementRepository;

    @EJB
    private StatutRemboursementRepository statutRemboursementRepository;

    @EJB
    private StatutPretRepository statutPretRepository;

    @EJB
    private PretRepository pretRepository;

    @EJB
    private FraisRepository fraisRepository;

    @EJB
    private MethodeRemboursementRepository methodeRemboursementRepository;

    /**
     * Récupère les informations du prochain remboursement à effectuer
     */
    public Map<String, Object> getInfosProchainRemboursement(Integer pretId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Récupérer le prêt
            Pret pret = pretRepository.findById(pretId);
            if (pret == null) {
                throw new RuntimeException("Prêt non trouvé avec l'ID: " + pretId);
            }

            // Récupérer le statut du prêt
            StatutPret statutPret = statutPretRepository.findById(pret.getIdStatutPret());
            result.put("statutPret", statutPret != null ? statutPret.getLibelle() : "Inconnu");

            // Compter le nombre de remboursements déjà effectués
            List<Remboursement> remboursementsEffectues = remboursementRepository.findByPretIdAndPaid(pretId);
            int nombreRemboursementsEffectues = remboursementsEffectues.size();

            // Récupérer le tableau d'amortissement
            List<AmortissementPret> amortissement = amortissementRepository.findByPretId(pretId);
            
            if (amortissement.isEmpty()) {
                throw new RuntimeException("Tableau d'amortissement non trouvé pour le prêt " + pretId);
            }

            // Trouver le prochain remboursement (période suivante)
            int prochainePeriode = nombreRemboursementsEffectues + 1;
            
            AmortissementPret prochainRemboursement = null;
            for (AmortissementPret amort : amortissement) {
                if (amort.getPeriode() == prochainePeriode) {
                    prochainRemboursement = amort;
                    break;
                }
            }

            if (prochainRemboursement == null) {
                // Prêt entièrement remboursé
                result.put("prochainRemboursement", null);
                
                // Vérifier si le statut du prêt doit être mis à jour
                if (pret.getIdStatutPret() != 2) { // 2 = "Remboursé"
                    pret.setIdStatutPret(2);
                    pretRepository.update(pret);
                }
                
                return result;
            }

            // Le prochain remboursement existe
            result.put("prochainRemboursement", prochainRemboursement);

            // Vérifier si c'est en retard
            LocalDate dateEcheance = prochainRemboursement.getDateEcheance();
            LocalDate dateActuelle = LocalDate.now();
            
            boolean enRetard = dateActuelle.isAfter(dateEcheance);
            result.put("enRetard", enRetard);

            BigDecimal fraisRetard = BigDecimal.ZERO;
            BigDecimal montantTotalAPayer = prochainRemboursement.getAnnuite();

            if (enRetard) {
                // Calculer les frais de retard (frais par jour * nombre de jours)
                long joursDeRetard = ChronoUnit.DAYS.between(dateEcheance, dateActuelle);
                Frais fraisRetardInfo = fraisRepository.findCurrentByNom("Frais de retard");
                if (fraisRetardInfo != null) {
                    BigDecimal fraisParJour = new BigDecimal(fraisRetardInfo.getValeur());
                    fraisRetard = fraisParJour.multiply(new BigDecimal(joursDeRetard));
                    montantTotalAPayer = montantTotalAPayer.add(fraisRetard);
                }
                
                // Calculer le total des frais déjà payés pour ce prêt
                BigDecimal totalFraisPaye = calculateTotalFraisPaye(pretId);
                result.put("totalFraisPaye", totalFraisPaye);
            }

            result.put("fraisRetard", fraisRetard);
            result.put("montantTotalAPayer", montantTotalAPayer);

            return result;

        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des informations de remboursement: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des informations de remboursement", e);
        }
    }

    /**
     * Récupère l'historique des remboursements d'un prêt
     */
    public List<Map<String, Object>> getHistoriqueRemboursements(Integer pretId) {
        List<Map<String, Object>> historique = new ArrayList<>();
        
        try {
            // Récupérer tous les remboursements du prêt
            List<Remboursement> remboursements = remboursementRepository.findByPretId(pretId);
            
            // Récupérer le tableau d'amortissement pour avoir les périodes
            List<AmortissementPret> amortissement = amortissementRepository.findByPretId(pretId);
            Map<Integer, AmortissementPret> amortissementMap = new HashMap<>();
            for (AmortissementPret amort : amortissement) {
                amortissementMap.put(amort.getPeriode(), amort);
            }

            for (Remboursement remb : remboursements) {
                Map<String, Object> item = new HashMap<>();
                
                // Trouver la période correspondante
                AmortissementPret amortPeriode = null;
                for (AmortissementPret amort : amortissement) {
                    if (amort.getId().equals(remb.getId())) { // Relation par ID ou autre logique
                        amortPeriode = amort;
                        break;
                    }
                }
                
                item.put("periode", amortPeriode != null ? amortPeriode.getPeriode() : "N/A");
                item.put("datePaiement", remb.getDatePaiement());
                item.put("montant", remb.getMontant());
                item.put("joursRetard", remb.getJoursRetard());
                
                // Récupérer le statut
                StatutRemboursement statut = statutRemboursementRepository.findById(remb.getIdStatutRemboursement());
                item.put("statutRemboursement", statut != null ? statut.getLibelle() : "Inconnu");
                
                // Récupérer la méthode
                MethodeRemboursement methode = methodeRemboursementRepository.findById(remb.getIdMethodeRemboursement());
                item.put("methodeRemboursement", methode != null ? methode.getLibelle() : "Inconnu");
                
                historique.add(item);
            }

            // Trier par période
            historique.sort((a, b) -> {
                Object periodeA = a.get("periode");
                Object periodeB = b.get("periode");
                if (periodeA instanceof Integer && periodeB instanceof Integer) {
                    return Integer.compare((Integer) periodeA, (Integer) periodeB);
                }
                return 0;
            });

            return historique;

        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération de l'historique des remboursements: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération de l'historique des remboursements", e);
        }
    }

    /**
     * Effectue un paiement de remboursement avec toute la logique métier
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Map<String, Object> effectuerRemboursement(Integer pretId, String datePaiement, 
                                                      BigDecimal montant, Integer idMethodeRemboursement) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            LOGGER.info("Traitement du remboursement pour le prêt " + pretId + 
                       ", montant: " + montant + ", date: " + datePaiement);

            // 1. Récupérer les informations du prochain remboursement
            Map<String, Object> infos = getInfosProchainRemboursement(pretId);
            AmortissementPret prochainRemboursement = (AmortissementPret) infos.get("prochainRemboursement");
            
            if (prochainRemboursement == null) {
                result.put("success", false);
                result.put("message", "Ce prêt est déjà entièrement remboursé.");
                return result;
            }

            // 2. Vérifications du montant
            BigDecimal montantAttendu = (BigDecimal) infos.get("montantTotalAPayer");
            if (montant.compareTo(montantAttendu) != 0) {
                result.put("success", false);
                result.put("message", "Le montant saisi (" + montant + "€) ne correspond pas au montant attendu (" + montantAttendu + "€).");
                return result;
            }

            // 3. Parse de la date de paiement
            LocalDate datePaiementLocal = LocalDate.parse(datePaiement, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate dateEcheance = prochainRemboursement.getDateEcheance();

            // 4. Calculer les jours de retard
            long joursRetard = 0;
            boolean enRetard = datePaiementLocal.isAfter(dateEcheance);
            if (enRetard) {
                joursRetard = ChronoUnit.DAYS.between(dateEcheance, datePaiementLocal);
            }

            // 5. Déterminer le statut du remboursement
            Integer idStatutRemboursement = joursRetard > 0 ? 2 : 1; // 1=Payé à temps, 2=En retard

            // 6. Créer l'enregistrement de remboursement
            Remboursement remboursement = new Remboursement();
            remboursement.setDatePaiement(datePaiementLocal);
            remboursement.setMontant(montant);
            remboursement.setJoursRetard((int) joursRetard);
            remboursement.setIdStatutRemboursement(idStatutRemboursement);
            remboursement.setIdMethodeRemboursement(idMethodeRemboursement);
            remboursement.setIdPret(pretId);

            remboursementRepository.create(remboursement);

            // 7. Vérifier si le prêt est maintenant entièrement remboursé
            List<Remboursement> tousRemboursements = remboursementRepository.findByPretIdAndPaid(pretId);
            List<AmortissementPret> toutAmortissement = amortissementRepository.findByPretId(pretId);
            
            boolean pretRembourse = tousRemboursements.size() >= toutAmortissement.size();

            if (pretRembourse) {
                // Mettre à jour le statut du prêt à "Remboursé"
                Pret pret = pretRepository.findById(pretId);
                pret.setIdStatutPret(2); // 2 = "Remboursé"
                pretRepository.update(pret);
                
                result.put("pretRembourse", true);
                result.put("message", "Remboursement effectué avec succès. Le prêt est maintenant entièrement remboursé !");
            } else {
                result.put("pretRembourse", false);
                String messageRetard = joursRetard > 0 ? 
                    " (avec " + joursRetard + " jour(s) de retard)" : " (à temps)";
                result.put("message", "Remboursement de la période " + prochainRemboursement.getPeriode() + 
                          " effectué avec succès" + messageRetard + ".");
            }

            result.put("success", true);

            LOGGER.info("Remboursement traité avec succès pour le prêt " + pretId);
            return result;

        } catch (Exception e) {
            LOGGER.severe("Erreur lors du traitement du remboursement: " + e.getMessage());
            result.put("success", false);
            result.put("message", "Erreur lors du traitement du paiement: " + e.getMessage());
            return result;
        }
    }

    /**
     * Calcule le total des frais payés pour un prêt donné
     * (somme de tous les frais de retard payés dans les remboursements passés)
     */
    private BigDecimal calculateTotalFraisPaye(Integer pretId) {
        try {
            // Récupérer tous les remboursements payés pour ce prêt
            List<Remboursement> remboursementsPayes = remboursementRepository.findByPretIdAndPaid(pretId);
            
            BigDecimal totalFrais = BigDecimal.ZERO;
            
            for (Remboursement remb : remboursementsPayes) {
                // Calculer les frais de retard pour ce remboursement s'il était en retard
                if (remb.getJoursRetard() > 0) {
                    // Récupérer les frais de retard applicables à la date de paiement
                    Frais fraisRetardInfo = fraisRepository.findCurrentByNom("Frais de retard");
                    if (fraisRetardInfo != null) {
                        BigDecimal fraisParJour = new BigDecimal(fraisRetardInfo.getValeur());
                        BigDecimal fraisRemboursement = fraisParJour.multiply(new BigDecimal(remb.getJoursRetard()));
                        totalFrais = totalFrais.add(fraisRemboursement);
                    }
                }
            }
            
            return totalFrais;
            
        } catch (Exception e) {
            LOGGER.severe("Erreur lors du calcul du total des frais payés: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
}
