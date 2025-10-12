package com.example.centralizer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.centralizer.services.CompteCourantProxyService;
import com.example.centralizer.models.compteCourantDTO.CompteCourant;
import com.example.centralizer.models.compteCourantDTO.Transaction;
import com.example.centralizer.models.compteCourantDTO.Transfert;
import java.util.List;

@Controller
public class CompteCourantController {
    @Autowired
    private CompteCourantProxyService compteCourantProxyService;

    @GetMapping("/comptes-courant")
    public String getComptesCourant(Model model) {
        try {
            model.addAttribute("comptesCourant", compteCourantProxyService.getAllComptesCourant());
        } catch (Exception e) {
            // En cas d'erreur de connexion au service
            model.addAttribute("comptesCourant", null);
            model.addAttribute("error", "Erreur lors de la récupération des comptes courant: " + e.getMessage());
        }
        return "comptes-courant/list";
    }

    @GetMapping("/comptes-courant/{id}")
    @ResponseBody
    public CompteCourant getCompteCourantById(@PathVariable int id) {
        return compteCourantProxyService.getCompteCourantById(id);
    }

    @PostMapping("/comptes-courant")
    @ResponseBody
    public CompteCourant createCompteCourant(@RequestBody CompteCourant compteCourant) {
        return compteCourantProxyService.createCompteCourant(compteCourant);
    }

    @PutMapping("/comptes-courant/{id}")
    @ResponseBody
    public CompteCourant updateCompteCourant(@PathVariable int id, @RequestBody CompteCourant compteCourant) {
        return compteCourantProxyService.updateCompteCourant(id, compteCourant);
    }

    @DeleteMapping("/comptes-courant/{id}")
    @ResponseBody
    public void deleteCompteCourant(@PathVariable int id) {
        compteCourantProxyService.deleteCompteCourant(id);
    }

    // Endpoints pour les transactions
    @GetMapping("/comptes-courant/{compteId}/transactions")
    @ResponseBody
    public List<Transaction> getTransactionsByCompteId(@PathVariable int compteId) {
        return compteCourantProxyService.getTransactionsByCompteId(compteId);
    }

    @PostMapping("/comptes-courant/{compteId}/transactions")
    @ResponseBody
    public Transaction createTransaction(@PathVariable int compteId, @RequestBody Transaction transaction) {
        return compteCourantProxyService.createTransaction(compteId, transaction);
    }

    // Endpoints pour les transferts
    @GetMapping("/comptes-courant/{compteId}/transferts")
    @ResponseBody
    public List<Transfert> getTransfertsByCompteId(@PathVariable int compteId) {
        return compteCourantProxyService.getTransfertsByCompteId(compteId);
    }

    @PostMapping("/transferts")
    @ResponseBody
    public Transfert createTransfert(@RequestBody Transfert transfert) {
        return compteCourantProxyService.createTransfert(transfert);
    }
}
