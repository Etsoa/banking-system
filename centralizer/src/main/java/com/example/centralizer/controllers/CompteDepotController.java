package com.example.centralizer.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.centralizer.services.CompteDepotProxyService;

@Controller
public class CompteDepotController {
    @Autowired
    private CompteDepotProxyService compteDepotProxyService;

    @GetMapping("/comptes-depot")
    public String getComptesDepot(Model model) {
        model.addAttribute("comptesDepot", compteDepotProxyService.getAllComptesDepot());
        return "comptesDepot";
    }

    @GetMapping("/comptes-depot/{id}")
    @ResponseBody
    public Object getCompteDepotById(@PathVariable int id) {
        return compteDepotProxyService.getCompteDepotById(id);
    }

    @PostMapping("/comptes-depot")
    @ResponseBody
    public Object createCompteDepot(@RequestBody Map<String, Object> compteDepot) {
        return compteDepotProxyService.createCompteDepot(compteDepot);
    }

    @PutMapping("/comptes-depot/{id}")
    @ResponseBody
    public Object updateCompteDepot(@PathVariable int id, @RequestBody Map<String, Object> compteDepot) {
        return compteDepotProxyService.updateCompteDepot(id, compteDepot);
    }

    @DeleteMapping("/comptes-depot/{id}")
    @ResponseBody
    public void deleteCompteDepot(@PathVariable int id) {
        compteDepotProxyService.deleteCompteDepot(id);
    }
    // test modif
}
