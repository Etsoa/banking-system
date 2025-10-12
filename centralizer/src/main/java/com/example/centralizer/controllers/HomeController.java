package com.example.centralizer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.centralizer.services.ClientService;

@Controller
public class HomeController {

    @Autowired
    private ClientService clientService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/clients")
    public String getClients(Model model) {
        try {
            model.addAttribute("clients", clientService.getAllClients());
        } catch (Exception e) {
            // En cas d'erreur de connexion à la base de données
            model.addAttribute("clients", null);
            model.addAttribute("error", "Erreur lors de la récupération des clients: " + e.getMessage());
        }
        return "clients";
    }
}