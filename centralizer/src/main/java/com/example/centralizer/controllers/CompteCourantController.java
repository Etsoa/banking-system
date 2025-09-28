package com.example.centralizer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.example.centralizer.services.CompteCourantProxyService;

@Controller
public class CompteCourantController {
    @Autowired
    private CompteCourantProxyService compteCourantProxyService;

    @GetMapping("/comptes-courant")
    public String getComptesCourant(Model model) {
        model.addAttribute("comptesCourant", compteCourantProxyService.getAllComptesCourant());
        return "comptesCourant";
    }

    @GetMapping("/comptes-courant/{id}")
    @ResponseBody
    public Object getCompteCourantById(@PathVariable int id) {
        return compteCourantProxyService.getCompteCourantById(id);
    }

    @PostMapping("/comptes-courant")
    @ResponseBody
    public Object createCompteCourant(@RequestBody Map<String, Object> compteCourant) {
        return compteCourantProxyService.createCompteCourant(compteCourant);
    }

    @PutMapping("/comptes-courant/{id}")
    @ResponseBody
    public Object updateCompteCourant(@PathVariable int id, @RequestBody Map<String, Object> compteCourant) {
        return compteCourantProxyService.updateCompteCourant(id, compteCourant);
    }

    @DeleteMapping("/comptes-courant/{id}")
    @ResponseBody
    public void deleteCompteCourant(@PathVariable int id) {
        compteCourantProxyService.deleteCompteCourant(id);
    }
}
