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
}
