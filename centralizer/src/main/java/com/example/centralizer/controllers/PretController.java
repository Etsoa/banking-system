package com.example.centralizer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.centralizer.services.PretService;
import com.example.centralizer.services.PretParametresService;
import com.example.centralizer.services.ClientService;
import com.example.centralizer.services.ExceptionHandlingService;
import com.example.centralizer.exceptions.ServerApplicationException;
import com.example.centralizer.models.pretDTO.Pret;
import com.example.centralizer.models.Client;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PretController {
    
    @Autowired
    private PretService pretService;

    @Autowired
    private PretParametresService pretParametresService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    @GetMapping("/prets")
    public String getPrets(@RequestParam(required = false) String clientId, Model model) {
        try {
            List<Pret> prets;
            if (clientId != null && !clientId.isEmpty()) {
                prets = pretService.getPretsByClientId(clientId);
                model.addAttribute("filterClientId", clientId);
            } else {
                prets = pretService.getAllPrets();
            }
            model.addAttribute("prets", prets);
        } catch (ServerApplicationException e) {
            String userMessage = exceptionHandlingService.getUserFriendlyMessage(e);
            model.addAttribute("prets", null);
            model.addAttribute("error", userMessage);
            model.addAttribute("errorDetails", e.getFormattedMessage());
        } catch (Exception e) {
            model.addAttribute("prets", null);
            model.addAttribute("error", "Erreur lors de la récupération des prêts: " + e.getMessage());
        }
        return "prets/list";
    }

    @PostMapping("/prets")
    @ResponseBody
    public Pret createPret(@RequestBody Pret pret) {
        try {
            return pretService.createPret(pret);
        } catch (ServerApplicationException e) {
            throw new RuntimeException(exceptionHandlingService.getUserFriendlyMessage(e));
        }
    }

    @GetMapping("/prets/client/")
    public String getPretsByClientId(@RequestParam(required = true) String clientId, Model model) {
        try {
            List<Pret> prets = pretService.getPretsByClientId(clientId);
            model.addAttribute("prets", prets);
            return "prets/list";
        } catch (ServerApplicationException e) {
            String userMessage = exceptionHandlingService.getUserFriendlyMessage(e);
            model.addAttribute("prets", null);
            model.addAttribute("error", userMessage);
            model.addAttribute("errorDetails", e.getFormattedMessage());
            return "prets/list";
        } catch (Exception e) {
            model.addAttribute("prets", null);
            model.addAttribute("error", "Erreur lors de la récupération des prêts pour le client " + clientId + " : " + e.getMessage());
            return "prets/list";
        }
    }

    @GetMapping("/prets/nouveau")
    public String showCreatePretForm(Model model) {
        try {
            model.addAttribute("pret", new Pret());
            
            // Charger les données de référence pour les listes déroulantes
            List<Client> clients = clientService.getAllClients();
            model.addAttribute("clients", clients);
            
            model.addAttribute("modalites", pretParametresService.getAllModalites());
            model.addAttribute("typesRemboursement", pretParametresService.getAllTypesRemboursement());
            
            return "prets/pret-form";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'affichage du formulaire: " + e.getMessage());
            return "redirect:/prets";
        }
    }

    @PostMapping("/prets/create")
    public String createPretForm(@ModelAttribute("pret") Pret pret, Model model, 
                                RedirectAttributes redirectAttributes) {
        try {
            // Valider les données obligatoires
            if (pret.getIdClient() == null || pret.getIdClient().trim().isEmpty()) {
                model.addAttribute("error", "L'ID du client est obligatoire");
                return "prets/pret-form";
            }
            
            if (pret.getMontant() == null || pret.getMontant().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                model.addAttribute("error", "Le montant doit être supérieur à zéro");
                return "prets/pret-form";
            }
            
            if (pret.getDureeMois() == null || pret.getDureeMois() <= 0) {
                model.addAttribute("error", "La durée en mois doit être supérieure à zéro");
                return "prets/pret-form";
            }

            // Appel au service avec la nouvelle signature
            Pret nouveauPret = pretService.createPretComplet(
                pret.getIdClient(), 
                pret.getMontant(), 
                pret.getDureeMois(),
                pret.getIdModalite(),
                pret.getIdTypeRemboursement()
            );
            
            if (nouveauPret != null) {
                redirectAttributes.addFlashAttribute("success", "Prêt créé avec succès avec tableau d'amortissement !");
                return "redirect:/prets";
            } else {
                model.addAttribute("error", "Erreur lors de la création du prêt");
                return "prets/pret-form";
            }
        } catch (ServerApplicationException e) {
            String userMessage = exceptionHandlingService.getUserFriendlyMessage(e);
            model.addAttribute("error", userMessage);
            model.addAttribute("errorDetails", e.getFormattedMessage());
            return "prets/pret-form";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la création du prêt: " + e.getMessage());
            return "prets/pret-form";
        }
    }

    /**
     * Endpoint AJAX pour récupérer la plage de durée selon le montant
     */
    @GetMapping("/prets/plage-duree")
    @ResponseBody
    public Map<String, Object> getPlageDureeByMontant(@RequestParam BigDecimal montant) {
        try {
            return pretParametresService.getPlageDureeByMontant(montant);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération de la plage de durée: " + e.getMessage());
            return error;
        }
    }

    /**
     * Afficher le formulaire de paiement d'un remboursement
     */
    @GetMapping("/prets/{id}/remboursement")
    public String showRemboursementForm(@PathVariable Long id, Model model) {
        try {
            // Récupérer les informations du prêt
            Pret pret = pretService.getPretById(id);
            model.addAttribute("pret", pret);
            
            // Récupérer les informations du prochain remboursement
            Map<String, Object> infosRemboursement = pretService.getInfosProchainRemboursement(id);
            
            if (infosRemboursement.containsKey("prochainRemboursement")) {
                model.addAttribute("prochainRemboursement", infosRemboursement.get("prochainRemboursement"));
                model.addAttribute("enRetard", infosRemboursement.get("enRetard"));
                model.addAttribute("fraisRetard", infosRemboursement.get("fraisRetard"));
                model.addAttribute("montantTotalAPayer", infosRemboursement.get("montantTotalAPayer"));
            }
            
            // Récupérer le statut du prêt
            model.addAttribute("pretStatut", infosRemboursement.get("statutPret"));
            
            // Récupérer les méthodes de remboursement
            List<Map<String, Object>> methodesRemboursement = pretParametresService.getMethodesRemboursement();
            model.addAttribute("methodesRemboursement", methodesRemboursement);
            
            // Récupérer l'historique des remboursements
            List<Map<String, Object>> historique = pretService.getHistoriqueRemboursements(id);
            model.addAttribute("historique", historique);
            
            return "prets/remboursement-form";
        } catch (ServerApplicationException e) {
            String userMessage = exceptionHandlingService.getUserFriendlyMessage(e);
            model.addAttribute("error", userMessage);
            return "prets/remboursement-form";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des informations: " + e.getMessage());
            return "prets/remboursement-form";
        }
    }

    /**
     * Traiter le paiement d'un remboursement
     */
    @PostMapping("/prets/{id}/payer")
    public String effectuerPaiement(
            @PathVariable Long id,
            @RequestParam String datePaiement,
            @RequestParam BigDecimal montant,
            @RequestParam Integer idMethodeRemboursement,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Effectuer le paiement via le service
            Map<String, Object> resultat = pretService.effectuerRemboursement(id, datePaiement, montant, idMethodeRemboursement);
            
            if ((Boolean) resultat.get("success")) {
                redirectAttributes.addFlashAttribute("success", resultat.get("message"));
                
                // Si le prêt est maintenant remboursé, rediriger vers la liste
                if ((Boolean) resultat.get("pretRembourse")) {
                    return "redirect:/prets";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", resultat.get("message"));
            }
            
            return "redirect:/prets/" + id + "/remboursement";
            
        } catch (ServerApplicationException e) {
            String userMessage = exceptionHandlingService.getUserFriendlyMessage(e);
            redirectAttributes.addFlashAttribute("error", userMessage);
            return "redirect:/prets/" + id + "/remboursement";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du paiement: " + e.getMessage());
            return "redirect:/prets/" + id + "/remboursement";
        }
    }
}