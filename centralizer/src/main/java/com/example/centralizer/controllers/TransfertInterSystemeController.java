package com.example.centralizer.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.centralizer.exceptions.ServerApplicationException;
import com.example.centralizer.models.Client;
import com.example.centralizer.models.compteCourantDTO.CompteCourant;
import com.example.centralizer.models.compteCourantDTO.CompteCourantAvecStatut;
import com.example.centralizer.models.compteDepotDTO.Compte;
import com.example.centralizer.services.ClientService;
import com.example.centralizer.services.CompteCourantService;
import com.example.centralizer.services.CompteDepotService;
import com.example.centralizer.services.ExceptionHandlingService;
import com.example.centralizer.services.TransfertInterSystemeService;

@Controller
public class TransfertInterSystemeController {
    
    @Autowired
    private ClientService clientService;

    @Autowired
    private CompteCourantService compteCourantService;

    @Autowired
    private CompteDepotService compteDepotService;

    @Autowired
    private TransfertInterSystemeService transfertInterSystemeService;

    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    @GetMapping("/transferts-inter-systeme")
    public String showTransfertInterSystemeForm(Model model) {
        try {
            // Récupérer tous les comptes courants
            List<CompteCourantAvecStatut> comptesCourant = compteCourantService.getAllComptesAvecStatut();
            if (comptesCourant == null) {
                comptesCourant = new ArrayList<>();
            }
            model.addAttribute("comptesCourant", comptesCourant);
            
            // Récupérer tous les comptes dépôt
            List<Compte> comptesDepot = compteDepotService.getAllComptes();
            if (comptesDepot == null) {
                comptesDepot = new ArrayList<>();
            }
            model.addAttribute("comptesDepot", comptesDepot);
            
            // Récupérer tous les clients pour afficher leurs noms
            List<Client> clients = clientService.getAllClients();
            Map<Integer, Client> clientsMap = new HashMap<>();
            if (clients != null) {
                for (Client client : clients) {
                    clientsMap.put(client.getId(), client);
                }
            }
            model.addAttribute("clientsMap", clientsMap);
            
            // Récupérer les transferts récents inter-systèmes
            try {
                List<Map<String, Object>> transfertsRecents = transfertInterSystemeService.getTransfertsRecents();
                model.addAttribute("transfertsRecents", transfertsRecents);
            } catch (Exception transfertEx) {
                // Si erreur avec les transferts, on continue sans bloquer le formulaire
                model.addAttribute("transfertsRecents", new ArrayList<>());
                model.addAttribute("transfertError", "Impossible de récupérer les transferts récents: " + transfertEx.getMessage());
            }
            
        } catch (ServerApplicationException e) {
            String userMessage = exceptionHandlingService.getUserFriendlyMessage(e);
            model.addAttribute("error", userMessage);
            model.addAttribute("errorDetails", e.getFormattedMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la préparation du formulaire de transfert inter-système: " + e.getMessage());
        }
        
        return "transferts-inter-systeme/transfert-form";
    }

    @PostMapping("/transferts-inter-systeme/effectuer")
    public String effectuerTransfertInterSysteme(
            @RequestParam String typeCompteEnvoyeur,
            @RequestParam String compteEnvoyeur,
            @RequestParam String typeCompteReceveur,
            @RequestParam String compteReceveur,
            @RequestParam double montant,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dateTransfert,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Validation des entrées
            if (typeCompteEnvoyeur.equals(typeCompteReceveur) && compteEnvoyeur.equals(compteReceveur)) {
                redirectAttributes.addFlashAttribute("error", "Impossible d'effectuer un transfert vers le même compte");
                return "redirect:/transferts-inter-systeme";
            }

            if (montant <= 0) {
                redirectAttributes.addFlashAttribute("error", "Le montant doit être supérieur à 0");
                return "redirect:/transferts-inter-systeme";
            }

            // Effectuer le transfert inter-système
            boolean success = transfertInterSystemeService.effectuerTransfertInterSysteme(
                typeCompteEnvoyeur, compteEnvoyeur,
                typeCompteReceveur, compteReceveur,
                BigDecimal.valueOf(montant), dateTransfert
            );
            
            if (success) {
                redirectAttributes.addFlashAttribute("success", 
                    String.format("Transfert inter-système effectué avec succès ! Montant: %.2f € de %s (%s) vers %s (%s)", 
                    montant, compteEnvoyeur, typeCompteEnvoyeur, compteReceveur, typeCompteReceveur));
            } else {
                redirectAttributes.addFlashAttribute("error", "Erreur lors du transfert inter-système");
            }
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Erreur de validation: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du transfert inter-système: " + e.getMessage());
        }
        
        return "redirect:/transferts-inter-systeme";
    }

    @GetMapping("/transferts-inter-systeme/historique")
    public String showHistoriqueTransferts(
            @RequestParam(required = false) String compteId,
            @RequestParam(required = false) String typeCompte,
            Model model) {
        
        try {
            List<Map<String, Object>> historique;
            
            if (compteId != null && !compteId.trim().isEmpty() && typeCompte != null && !typeCompte.trim().isEmpty()) {
                // Historique pour un compte spécifique
                historique = transfertInterSystemeService.getHistoriqueParCompte(compteId, typeCompte);
                model.addAttribute("compteFiltre", compteId);
                model.addAttribute("typeCompteFiltre", typeCompte);
                
                // Récupérer les détails du compte pour l'affichage
                if ("compteCourant".equals(typeCompte)) {
                    CompteCourant compte = compteCourantService.getCompteById(compteId);
                    model.addAttribute("compteDetails", compte);
                } else if ("compteDepot".equals(typeCompte)) {
                    Compte compte = compteDepotService.getCompteById(compteId);
                    model.addAttribute("compteDetails", compte);
                }
            } else {
                // Historique global
                historique = transfertInterSystemeService.getTousLesTransferts();
            }
            
            model.addAttribute("historique", historique);
            
            // Récupérer tous les clients pour afficher leurs noms
            List<Client> clients = clientService.getAllClients();
            Map<Integer, Client> clientsMap = new HashMap<>();
            if (clients != null) {
                for (Client client : clients) {
                    clientsMap.put(client.getId(), client);
                }
            }
            model.addAttribute("clientsMap", clientsMap);
            
        } catch (Exception e) {
            model.addAttribute("historique", new ArrayList<>());
            model.addAttribute("error", "Erreur lors de la récupération de l'historique: " + e.getMessage());
        }
        
        return "transferts-inter-systeme/historique";
    }
}