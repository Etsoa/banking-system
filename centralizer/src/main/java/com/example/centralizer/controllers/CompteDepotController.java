package com.example.centralizer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.centralizer.services.CompteDepotProxyService;
import com.example.centralizer.models.compteDepotDTO.Compte;
import com.example.centralizer.models.compteDepotDTO.Transaction;
import com.example.centralizer.models.compteDepotDTO.Transfert;
import java.util.List;

@Controller
public class CompteDepotController {
    @Autowired
    private CompteDepotProxyService compteDepotProxyService;

    @GetMapping("/comptes-depot")
    public String getComptesDepot(Model model) {
        try {
            model.addAttribute("comptesDepot", compteDepotProxyService.getAllComptesDepot());
        } catch (Exception e) {
            // En cas d'erreur de connexion au service
            model.addAttribute("comptesDepot", null);
            model.addAttribute("error", "Erreur lors de la récupération des comptes dépôt: " + e.getMessage());
        }
        return "comptesDepot";
    }

    @GetMapping("/comptes-depot/{id}")
    @ResponseBody
    public Compte getCompteDepotById(@PathVariable int id) {
        return compteDepotProxyService.getCompteDepotById(id);
    }

    @PostMapping("/comptes-depot")
    @ResponseBody
    public Compte createCompteDepot(@RequestBody Compte compte) {
        return compteDepotProxyService.createCompteDepot(compte);
    }

    @PutMapping("/comptes-depot/{id}")
    @ResponseBody
    public Compte updateCompteDepot(@PathVariable int id, @RequestBody Compte compte) {
        return compteDepotProxyService.updateCompteDepot(id, compte);
    }

    @DeleteMapping("/comptes-depot/{id}")
    @ResponseBody
    public void deleteCompteDepot(@PathVariable int id) {
        compteDepotProxyService.deleteCompteDepot(id);
    }

    // Endpoints pour les transactions
    @GetMapping("/comptes-depot/{compteId}/transactions")
    @ResponseBody
    public List<Transaction> getTransactionsByCompteId(@PathVariable int compteId) {
        return compteDepotProxyService.getTransactionsByCompteId(compteId);
    }

    @PostMapping("/comptes-depot/{compteId}/transactions")
    @ResponseBody
    public Transaction createTransaction(@PathVariable int compteId, @RequestBody Transaction transaction) {
        return compteDepotProxyService.createTransaction(compteId, transaction);
    }

    // Endpoints pour les transferts
    @GetMapping("/comptes-depot/{compteId}/transferts")
    @ResponseBody
    public List<Transfert> getTransfertsByCompteId(@PathVariable int compteId) {
        return compteDepotProxyService.getTransfertsByCompteId(compteId);
    }

    @PostMapping("/transferts-depot")
    @ResponseBody
    public Transfert createTransfert(@RequestBody Transfert transfert) {
        return compteDepotProxyService.createTransfert(transfert);
    }
}
