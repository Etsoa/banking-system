package com.example.centralizer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.centralizer.services.PretProxyService;
import com.example.centralizer.models.pretDTO.Pret;
import com.example.centralizer.models.pretDTO.Remboursement;
import java.util.List;

@Controller
public class PretController {
    @Autowired
    private PretProxyService pretProxyService;

    @GetMapping("/prets")
    public String getPrets(Model model) {
        try {
            model.addAttribute("prets", pretProxyService.getAllPrets());
        } catch (Exception e) {
            // En cas d'erreur de connexion au service
            model.addAttribute("prets", null);
            model.addAttribute("error", "Erreur lors de la récupération des prêts: " + e.getMessage());
        }
        return "prets";
    }

    @GetMapping("/prets/{id}")
    @ResponseBody
    public Pret getPretById(@PathVariable int id) {
        return pretProxyService.getPretById(id);
    }

    @PostMapping("/prets")
    @ResponseBody
    public Pret createPret(@RequestBody Pret pret) {
        return pretProxyService.createPret(pret);
    }

    @PutMapping("/prets/{id}")
    @ResponseBody
    public Pret updatePret(@PathVariable int id, @RequestBody Pret pret) {
        return pretProxyService.updatePret(id, pret);
    }

    @DeleteMapping("/prets/{id}")
    @ResponseBody
    public void deletePret(@PathVariable int id) {
        pretProxyService.deletePret(id);
    }

    // Endpoints pour les remboursements
    @GetMapping("/prets/{pretId}/remboursements")
    @ResponseBody
    public List<Remboursement> getRemboursementsByPretId(@PathVariable int pretId) {
        return pretProxyService.getRemboursementsByPretId(pretId);
    }

    @PostMapping("/prets/{pretId}/remboursements")
    @ResponseBody
    public Remboursement createRemboursement(@PathVariable int pretId, @RequestBody Remboursement remboursement) {
        return pretProxyService.createRemboursement(pretId, remboursement);
    }
}
