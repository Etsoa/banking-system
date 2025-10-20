package com.example.centralizer.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.centralizer.exceptions.ServerApplicationException;
import com.example.centralizer.models.Client;
import com.example.centralizer.models.compteDepotDTO.Compte;
import com.example.centralizer.models.compteDepotDTO.Transaction;
import com.example.centralizer.models.compteDepotDTO.Transfert;
import com.example.centralizer.models.compteDepotDTO.TransfertAvecFrais;
import com.example.centralizer.services.ClientService;
import com.example.centralizer.services.CompteDepotService;
import com.example.centralizer.services.ExceptionHandlingService;
import com.example.centralizer.services.TransactionCompteDepotService;

@Controller
public class CompteDepotController {
    
    @Autowired
    private ClientService clientService;

    @Autowired
    private CompteDepotService compteDepotService;

    @Autowired
    private TransactionCompteDepotService transactionService;

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

    @GetMapping("/comptes-depot/new")
    public String showCreateCompteDepotForm(Model model) {
        try {
            // Récupérer la liste des clients pour la liste déroulante
            List<Client> clients = clientService.getAllClients();
            model.addAttribute("clients", clients);
            model.addAttribute("compteDepot", new Compte());
            return "comptes-depot/compte-form";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des clients: " + e.getMessage());
            return "redirect:/comptes-depot";
        }
    }

    @PostMapping("/comptes-depot")
    public String createCompteDepot(@ModelAttribute("compteDepot") Compte compteDepot, 
                                   Model model, 
                                   RedirectAttributes redirectAttributes) {
        try {
            // Définir la date d'ouverture au moment actuel
            compteDepot.setDateOuverture(LocalDateTime.now());
            
            // Créer le compte via le service
            Compte nouveauCompte = compteDepotService.createCompte(compteDepot);
            
            if (nouveauCompte != null) {
                redirectAttributes.addFlashAttribute("success", "Compte dépôt créé avec succès !");
                return "redirect:/comptes-depot";
            } else {
                model.addAttribute("error", "Erreur lors de la création du compte dépôt");
                model.addAttribute("clients", clientService.getAllClients());
                return "comptes-depot/compte-form";
            }
        } catch (ServerApplicationException e) {
            // Gestion spécifique des erreurs de serveur
            String userMessage = exceptionHandlingService.getUserFriendlyMessage(e);
            model.addAttribute("error", userMessage);
            model.addAttribute("errorDetails", e.getFormattedMessage());
            try {
                model.addAttribute("clients", clientService.getAllClients());
            } catch (Exception clientEx) {
                model.addAttribute("error", "Erreur lors de la récupération des clients: " + clientEx.getMessage());
            }
            return "comptes-depot/compte-form";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la création du compte dépôt: " + e.getMessage());
            try {
                model.addAttribute("clients", clientService.getAllClients());
            } catch (Exception clientEx) {
                model.addAttribute("error", "Erreur lors de la récupération des clients: " + clientEx.getMessage());
            }
            return "comptes-depot/compte-form";
        }
    }

    @GetMapping("/comptes-depot/details")
    @ResponseBody
    public Compte getCompteDepotById(@RequestParam String id) {
        try {
            return compteDepotService.getCompteById(id);
        } catch (ServerApplicationException e) {
            throw new RuntimeException(exceptionHandlingService.getUserFriendlyMessage(e));
        }
    }

    @GetMapping("/comptes-depot/transactions")
    public String getTransactionsByCompte(@RequestParam String compteId, 
                                        @RequestParam(required = false) Integer typeId,
                                        Model model) {
        try {
            // Récupérer le compte
            Compte compte = compteDepotService.getCompteById(compteId);
            model.addAttribute("compte", compte);
            
            // Récupérer le client
            Client client = clientService.getClientById(compte.getIdClient());
            model.addAttribute("client", client);
            
            // Récupérer les transactions avec frais
            List<com.example.centralizer.models.compteDepotDTO.TransactionAvecFrais> transactions;
            if (typeId != null) {
                // Pour l'instant, utiliser la méthode normale et convertir plus tard
                transactions = transactionService.getTransactionsByCompteAvecFrais(compteId);
                model.addAttribute("filtreType", typeId);
            } else {
                transactions = transactionService.getTransactionsByCompteAvecFrais(compteId);
            }
            model.addAttribute("transactions", transactions);
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des transactions: " + e.getMessage());
        }
        
        return "comptes-depot/transactions";
    }

    @PostMapping("/comptes-depot/transactions/add")
    public String addTransaction(
            @RequestParam String compteId,
            @RequestParam Integer typeTransaction,
            @RequestParam double montant,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dateTransaction,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Créer la transaction
            Transaction transaction = new Transaction();
            transaction.setIdCompte(compteId);
            transaction.setIdTypeTransaction(typeTransaction);
            transaction.setMontant(BigDecimal.valueOf(montant));
            transaction.setDateTransaction(dateTransaction);

            // Appeler le service pour créer la transaction
            Transaction result = transactionService.createTransaction(transaction);
            
            if (result != null) {
                redirectAttributes.addFlashAttribute("success", "Transaction ajoutée avec succès !");
            } else {
                redirectAttributes.addFlashAttribute("error", "Erreur lors de l'ajout de la transaction");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'ajout de la transaction: " + e.getMessage());
        }
        
        return "redirect:/comptes-depot/transactions?compteId=" + compteId;
    }

    @GetMapping("/comptes-depot/transferts")
    public String showTransfertForm(@RequestParam String compteId, Model model) {
        try {
            // Récupérer le compte émetteur
            Compte compte = compteDepotService.getCompteById(compteId);
            model.addAttribute("compteEnvoyeur", compte);
            model.addAttribute("compteId", compteId);
            
            // Récupérer tous les comptes pour le choix du destinataire
            List<Compte> comptes = compteDepotService.getAllComptes();
            // Exclure le compte envoyeur de la liste
            comptes.removeIf(c -> c.getIdCompte().equals(compteId));
            model.addAttribute("comptesDisponibles", comptes);
            
            // Récupérer tous les clients pour afficher leurs noms
            List<Client> clients = clientService.getAllClients();
            Map<Integer, Client> clientsMap = new HashMap<>();
            for (Client client : clients) {
                clientsMap.put(client.getId(), client);
            }
            model.addAttribute("clientsMap", clientsMap);
            
            // Créer une map des comptes pour l'affichage des transferts
            Map<String, Compte> comptesMap = new HashMap<>();
            for (Compte c : comptes) {
                comptesMap.put(c.getIdCompte(), c);
            }
            comptesMap.put(compte.getIdCompte(), compte); // Ajouter le compte envoyeur
            model.addAttribute("comptesMap", comptesMap);
            
            // Récupérer les transferts récents du compte avec frais (limités aux 5 derniers)
            try {
                List<TransfertAvecFrais> tousTransferts = transactionService.getAllTransfertsAvecFrais();
                List<TransfertAvecFrais> transfertsRecents = tousTransferts.stream()
                    .filter(t -> t.getCompteEnvoyeur().equals(compteId) || t.getCompteReceveur().equals(compteId))
                    .sorted((t1, t2) -> t2.getDateTransfert().compareTo(t1.getDateTransfert()))
                    .limit(5)
                    .collect(Collectors.toList());
                model.addAttribute("transferts", transfertsRecents);
            } catch (Exception transfertEx) {
                // Si erreur avec les transferts, on continue sans bloquer le formulaire
                model.addAttribute("transferts", new ArrayList<>());
            }
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la préparation du formulaire de transfert: " + e.getMessage());
        }
        
        return "comptes-depot/transfert-form";
    }

    @PostMapping("/comptes-depot/transferts/add")
    public String addTransfert(
            @RequestParam String compteEnvoyeur,
            @RequestParam String compteReceveur,
            @RequestParam double montant,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dateTransfert,
            RedirectAttributes redirectAttributes) {
        try {
            // Appeler le service pour créer le transfert avec la date
            Transfert result = transactionService.createTransfert(compteEnvoyeur, compteReceveur, BigDecimal.valueOf(montant), dateTransfert);
            
            if (result != null) {
                redirectAttributes.addFlashAttribute("success", "Transfert effectué avec succès !");
            } else {
                redirectAttributes.addFlashAttribute("error", "Erreur lors du transfert");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du transfert: " + e.getMessage());
        }
        
        return "redirect:/comptes-depot/transferts?compteId=" + compteEnvoyeur;
    }

    // Redirection vers le formulaire de transfert principal
    @GetMapping("/comptes-depot/transferts/list")
    public String redirectToTransfertForm(@RequestParam String compteId) {
        return "redirect:/comptes-depot/transferts?compteId=" + compteId;
    }
    
    private Map<String, Object> calculateTransfertStats(List<Transfert> transferts, String compteId) {
        Map<String, Object> stats = new HashMap<>();
        
        long nombreEmis = transferts.stream()
            .filter(t -> t.getEnvoyer().equals(compteId))
            .count();
            
        long nombreRecus = transferts.stream()
            .filter(t -> t.getReceveur().equals(compteId))
            .count();
            
        BigDecimal montantTotalEmis = transferts.stream()
            .filter(t -> t.getEnvoyer().equals(compteId))
            .map(Transfert::getMontant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal montantTotalRecus = transferts.stream()
            .filter(t -> t.getReceveur().equals(compteId))
            .map(Transfert::getMontant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        stats.put("nombreEmis", nombreEmis);
        stats.put("nombreRecus", nombreRecus);
        stats.put("montantTotalEmis", montantTotalEmis);
        stats.put("montantTotalRecus", montantTotalRecus);
        
        return stats;
    }

    @PutMapping("/comptes-depot/depot")
    @ResponseBody
    public Compte effectuerDepot(
            @RequestParam String compteId, 
            @RequestParam double montant,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dateTransaction) {
        try {
            // Utiliser le système de transaction pour effectuer un dépôt
            Transaction transaction = new Transaction();
            transaction.setIdCompte(compteId);
            transaction.setIdTypeTransaction(1); // Type 1 = Dépôt
            transaction.setMontant(BigDecimal.valueOf(montant));
            transaction.setDateTransaction(dateTransaction);
            
            transactionService.createTransaction(transaction);
            
            // Retourner le compte mis à jour
            return compteDepotService.getCompteById(compteId);
        } catch (ServerApplicationException e) {
            throw new RuntimeException(exceptionHandlingService.getUserFriendlyMessage(e));
        }
    }

    @PutMapping("/comptes-depot/retrait")
    @ResponseBody
    public Compte effectuerRetrait(
            @RequestParam String compteId, 
            @RequestParam double montant,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dateTransaction) {
        try {
            // Utiliser le système de transaction pour effectuer un retrait
            Transaction transaction = new Transaction();
            transaction.setIdCompte(compteId);
            transaction.setIdTypeTransaction(2); // Type 2 = Retrait
            transaction.setMontant(BigDecimal.valueOf(montant));
            transaction.setDateTransaction(dateTransaction);
            
            transactionService.createTransaction(transaction);
            
            // Retourner le compte mis à jour
            return compteDepotService.getCompteById(compteId);
        } catch (ServerApplicationException e) {
            throw new RuntimeException(exceptionHandlingService.getUserFriendlyMessage(e));
        }
    }
}
