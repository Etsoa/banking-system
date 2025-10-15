package com.example.centralizer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.centralizer.services.CompteDepotService;
import com.example.centralizer.services.ExceptionHandlingService;
import com.example.centralizer.exceptions.ServerApplicationException;
import com.example.centralizer.models.compteDepotDTO.Compte;
import java.util.List;

@Controller
public class CompteDepotController {
    
    @Autowired
    private CompteDepotService compteDepotService;

    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    @GetMapping("/comptes-depot")
    public String getComptesDepot(Model model) {
        try {
            List<Compte> comptesDepot = compteDepotService.getAllComptes();
            model.addAttribute("comptesDepot", comptesDepot);
        } catch (ServerApplicationException e) {
            String userMessage = exceptionHandlingService.getUserFriendlyMessage(e);
            model.addAttribute("comptesDepot", null);
            model.addAttribute("error", userMessage);
            model.addAttribute("errorDetails", e.getFormattedMessage());
        } catch (Exception e) {
            model.addAttribute("comptesDepot", null);
            model.addAttribute("error", "Erreur lors de la récupération des comptes dépôt: " + e.getMessage());
        }
        return "comptes-depot/list";
    }

    @GetMapping("/comptes-depot/details")
    @ResponseBody
    public Compte getCompteDepotById(@RequestParam int id) {
        try {
            return compteDepotService.getCompteById(id);
        } catch (ServerApplicationException e) {
            throw new RuntimeException(exceptionHandlingService.getUserFriendlyMessage(e));
        }
    }

    @PostMapping("/comptes-depot")
    @ResponseBody
    public Compte createCompteDepot(@RequestBody Compte compte) {
        try {
            return compteDepotService.createCompte(compte);
        } catch (ServerApplicationException e) {
            throw new RuntimeException(exceptionHandlingService.getUserFriendlyMessage(e));
        }
    }

    /* ENDPOINTS NON IMPLÉMENTÉS DANS LE SERVEUR - COMMENTÉS
    @PutMapping("/comptes-depot/update")
    @ResponseBody
    public Compte updateCompteDepot(@RequestParam int id, @RequestBody Compte compte) {
        try {
            return compteDepotService.updateCompte((long) id, compte);
        } catch (ServerApplicationException e) {
            throw new RuntimeException(exceptionHandlingService.getUserFriendlyMessage(e));
        }
    }

    @PutMapping("/comptes-depot/depot")
    @ResponseBody
    public Compte effectuerDepot(@RequestParam int id, @RequestParam double montant) {
        try {
            return compteDepotService.effectuerDepot((long) id, montant);
        } catch (ServerApplicationException e) {
            throw new RuntimeException(exceptionHandlingService.getUserFriendlyMessage(e));
        }
    }

    @PutMapping("/comptes-depot/retrait")
    @ResponseBody
    public Compte effectuerRetrait(@RequestParam int id, @RequestParam double montant) {
        try {
            return compteDepotService.effectuerRetrait((long) id, montant);
        } catch (ServerApplicationException e) {
            throw new RuntimeException(exceptionHandlingService.getUserFriendlyMessage(e));
        }
    }
    */
}
