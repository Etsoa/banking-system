package com.example.centralizer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.centralizer.services.ClientService;
import com.example.centralizer.models.Client;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
            List<Client> clients = clientService.getAllClients();
            model.addAttribute("clients", clients);
            
            // Ajouter les statuts pour chaque client
            Map<Integer, String> statutsClients = new HashMap<>();
            for (Client client : clients) {
                statutsClients.put(client.getId(), clientService.getStatutClient(client.getId()));
            }
            model.addAttribute("statutsClients", statutsClients);
            
        } catch (Exception e) {
            // En cas d'erreur de connexion à la base de données
            model.addAttribute("clients", null);
            model.addAttribute("error", "Erreur lors de la récupération des clients: " + e.getMessage());
        }
        return "clients/list";
    }

    @GetMapping("/clients/new")
    public String showCreateClientForm(Model model) {
        model.addAttribute("client", new Client());
        return "clients/client-form";
    }

    @PostMapping("/clients")
    public String createClient(@ModelAttribute("client") Client client, 
                              Model model, 
                              RedirectAttributes redirectAttributes) {
        
        try {
            clientService.saveClient(client);
            redirectAttributes.addFlashAttribute("success", "Client créé avec succès !");
            return "redirect:/clients";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la création du client: " + e.getMessage());
            return "clients/client-form";
        }
    }

    @GetMapping("/clients/edit")
    public String showEditClientForm(@RequestParam("id") Integer id, Model model) {
        try {
            Client client = clientService.getClientById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable avec l'ID: " + id));
            model.addAttribute("client", client);
            
            // Ajouter le statut actuel du client
            String statutActuel = clientService.getStatutClient(id);
            model.addAttribute("statutActuel", statutActuel);
            
            return "clients/client-edit";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération du client: " + e.getMessage());
            return "redirect:/clients";
        }
    }

    @PostMapping("/clients/edit")
    public String updateClient(@RequestParam("id") Integer id, 
                              @RequestParam(value = "nouveauStatut", required = false) String nouveauStatut,
                              @ModelAttribute("client") Client clientForm, 
                              Model model, 
                              RedirectAttributes redirectAttributes) {
        
        try {
            // Récupérer le client existant
            Client clientExistant = clientService.getClientById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable avec l'ID: " + id));
            
            // Mettre à jour uniquement les champs non-vides
            if (clientForm.getNom() != null && !clientForm.getNom().trim().isEmpty()) {
                clientExistant.setNom(clientForm.getNom());
            }
            if (clientForm.getPrenom() != null && !clientForm.getPrenom().trim().isEmpty()) {
                clientExistant.setPrenom(clientForm.getPrenom());
            }
            if (clientForm.getDateNaissance() != null) {
                clientExistant.setDateNaissance(clientForm.getDateNaissance());
            }
            if (clientForm.getEmail() != null && !clientForm.getEmail().trim().isEmpty()) {
                clientExistant.setEmail(clientForm.getEmail());
            }
            if (clientForm.getTelephone() != null && !clientForm.getTelephone().trim().isEmpty()) {
                clientExistant.setTelephone(clientForm.getTelephone());
            }
            if (clientForm.getAdresse() != null && !clientForm.getAdresse().trim().isEmpty()) {
                clientExistant.setAdresse(clientForm.getAdresse());
            }
            
            // Sauvegarder le client modifié
            clientService.saveClient(clientExistant);
            
            // Si un nouveau statut est fourni et différent de l'actuel, l'enregistrer
            if (nouveauStatut != null && !nouveauStatut.trim().isEmpty()) {
                String statutActuel = clientService.getStatutClient(id);
                if (!nouveauStatut.equals(statutActuel)) {
                    clientService.changerStatutClient(id, nouveauStatut);
                }
            }
            
            redirectAttributes.addFlashAttribute("success", "Client modifié avec succès !");
            return "redirect:/clients";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la modification du client: " + e.getMessage());
            // Recharger les données pour le formulaire
            try {
                Client client = clientService.getClientById(id).orElse(new Client());
                model.addAttribute("client", client);
                String statutActuel = clientService.getStatutClient(id);
                model.addAttribute("statutActuel", statutActuel);
            } catch (Exception ex) {
                // Si on ne peut pas recharger, rediriger vers la liste
                return "redirect:/clients";
            }
            return "clients/client-edit";
        }
    }

    @GetMapping("/clients/view")
    public String viewClient(@RequestParam("id") Integer id, Model model) {
        try {
            Client client = clientService.getClientById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable avec l'ID: " + id));
            model.addAttribute("client", client);
            return "clients/client-view";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération du client: " + e.getMessage());
            return "redirect:/clients";
        }
    }

    @PostMapping("/clients/delete")
    public String deleteClient(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            // Utiliser la méthode deleteClient du service qui gère l'historique
            clientService.deleteClient(id);
            
            redirectAttributes.addFlashAttribute("success", "Client marqué comme inactif avec succès !");
            return "redirect:/clients";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la désactivation du client: " + e.getMessage());
            return "redirect:/clients";
        }
    }
}