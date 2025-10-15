package com.example.centralizer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.centralizer.services.PretService;
import com.example.centralizer.services.ExceptionHandlingService;
import com.example.centralizer.exceptions.ServerApplicationException;
import com.example.centralizer.models.pretDTO.Pret;
import java.util.List;

@Controller
public class PretController {
    
    @Autowired
    private PretService pretService;

    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    @GetMapping("/prets")
    public String getPrets(Model model) {
        try {
            List<Pret> prets = pretService.getAllPrets();
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

    /* ENDPOINT NON IMPLÉMENTÉ DANS LE SERVEUR - COMMENTÉ
    @GetMapping("/prets/{id}")
    @ResponseBody
    public Pret getPretById(@PathVariable int id) {
        try {
            return pretService.getPretById((long) id);
        } catch (ServerApplicationException e) {
            throw new RuntimeException(exceptionHandlingService.getUserFriendlyMessage(e));
        }
    }
    */

    @PostMapping("/prets")
    @ResponseBody
    public Pret createPret(@RequestBody Pret pret) {
        try {
            return pretService.createPret(pret);
        } catch (ServerApplicationException e) {
            throw new RuntimeException(exceptionHandlingService.getUserFriendlyMessage(e));
        }
    }

    /* ENDPOINTS NON IMPLÉMENTÉS DANS LE SERVEUR - COMMENTÉS
    @PutMapping("/prets/{id}")
    @ResponseBody
    public Pret updatePret(@PathVariable int id, @RequestBody Pret pret) {
        try {
            return pretService.updatePret((long) id, pret);
        } catch (ServerApplicationException e) {
            throw new RuntimeException(exceptionHandlingService.getUserFriendlyMessage(e));
        }
    }

    @PutMapping("/prets/{id}/remboursement")
    @ResponseBody
    public Pret effectuerRemboursement(@PathVariable int id, @RequestParam double montant) {
        try {
            return pretService.effectuerRemboursement((long) id, montant);
        } catch (ServerApplicationException e) {
            throw new RuntimeException(exceptionHandlingService.getUserFriendlyMessage(e));
        }
    }
    */
}
