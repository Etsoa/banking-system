package com.example.centralizer.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
            
            // Récupérer les transactions
            List<Transaction> transactions;
            if (typeId != null) {
                transactions = transactionService.getTransactionsByCompteAndType(compteId, typeId);
                model.addAttribute("filtreType", typeId);
            } else {
                transactions = transactionService.getTransactionsByCompte(compteId);
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
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dateTransaction,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Créer la transaction
            Transaction transaction = new Transaction();
            transaction.setIdCompte(compteId);
            transaction.setIdTypeTransaction(typeTransaction);
            transaction.setMontant(BigDecimal.valueOf(montant));
            
            // Utiliser la date fournie ou la date actuelle si non fournie
            if (dateTransaction != null) {
                transaction.setDateTransaction(dateTransaction);
            } else {
                transaction.setDateTransaction(LocalDateTime.now());
            }

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
            model.addAttribute("compteEmetteur", compte);
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
            
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la préparation du formulaire de transfert: " + e.getMessage());
        }
        
        return "comptes-depot/transfert-form";
    }

    @PostMapping("/comptes-depot/transferts/execute")
    public String executeTransfert(
            @RequestParam String compteEmetteurId,
            @RequestParam String compteRecepteurId,
            @RequestParam double montant,
            RedirectAttributes redirectAttributes) {
        try {
            // Appeler le service pour créer le transfert
            Transfert result = transactionService.createTransfert(compteEmetteurId, compteRecepteurId, BigDecimal.valueOf(montant));
            
            if (result != null) {
                redirectAttributes.addFlashAttribute("success", "Transfert effectué avec succès !");
            } else {
                redirectAttributes.addFlashAttribute("error", "Erreur lors du transfert");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du transfert: " + e.getMessage());
        }
        
        return "redirect:/comptes-depot/transactions?compteId=" + compteEmetteurId;
    }

    @PostMapping("/comptes-depot/transferts/add")
    public String addTransfert(
            @RequestParam String compteEnvoyeur,
            @RequestParam String compteReceveur,
            @RequestParam double montant,
            RedirectAttributes redirectAttributes) {
        try {
            // Appeler le service pour créer le transfert
            Transfert result = transactionService.createTransfert(compteEnvoyeur, compteReceveur, BigDecimal.valueOf(montant));
            
            if (result != null) {
                redirectAttributes.addFlashAttribute("success", "Transfert effectué avec succès !");
            } else {
                redirectAttributes.addFlashAttribute("error", "Erreur lors du transfert");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du transfert: " + e.getMessage());
        }
        
        return "redirect:/comptes-depot/transactions?compteId=" + compteEnvoyeur;
    }

    @GetMapping("/comptes-depot/transferts/list")
    public String getTransferts(@RequestParam(required = false) String compteId, 
                               @RequestParam(required = false) String direction,
                               Model model) {
        try {
            List<Transfert> transferts;
            if (compteId != null && !compteId.trim().isEmpty()) {
                // Récupérer tous les transferts via le service complet
                List<Transfert> tousTransferts = transactionService.getAllTransferts();
                
                // Filtrer pour ce compte
                transferts = tousTransferts.stream()
                    .filter(t -> t.getEnvoyer().equals(compteId) || t.getReceveur().equals(compteId))
                    .collect(Collectors.toList());
                
                // Calculer les statistiques
                Map<String, Object> stats = calculateTransfertStats(transferts, compteId);
                model.addAttribute("stats", stats);
                
                // Appliquer le filtre de direction si spécifié
                if ("emis".equals(direction)) {
                    transferts = transferts.stream()
                        .filter(t -> t.getEnvoyer().equals(compteId))
                        .collect(Collectors.toList());
                    model.addAttribute("filtreType", "emis");
                } else if ("recus".equals(direction)) {
                    transferts = transferts.stream()
                        .filter(t -> t.getReceveur().equals(compteId))
                        .collect(Collectors.toList());
                    model.addAttribute("filtreType", "recus");
                }
                
                Compte compte = compteDepotService.getCompteById(compteId);
                model.addAttribute("compte", compte);
                model.addAttribute("compteId", compteId);
            } else {
                transferts = transactionService.getAllTransferts();
            }
            
            // Trier par date décroissante
            transferts.sort((t1, t2) -> t2.getDateTransfert().compareTo(t1.getDateTransfert()));
            model.addAttribute("transferts", transferts);
            
        } catch (Exception e) {
            model.addAttribute("transferts", null);
            model.addAttribute("error", "Erreur lors de la récupération des transferts: " + e.getMessage());
        }
        
        return "comptes-depot/transferts";
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
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dateTransaction) {
        try {
            // Utiliser le système de transaction pour effectuer un dépôt
            Transaction transaction = new Transaction();
            transaction.setIdCompte(compteId);
            transaction.setIdTypeTransaction(1); // Type 1 = Dépôt
            transaction.setMontant(BigDecimal.valueOf(montant));
            
            // Utiliser la date fournie ou la date actuelle si non fournie
            if (dateTransaction != null) {
                transaction.setDateTransaction(dateTransaction);
            } else {
                transaction.setDateTransaction(LocalDateTime.now());
            }
            
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
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dateTransaction) {
        try {
            // Utiliser le système de transaction pour effectuer un retrait
            Transaction transaction = new Transaction();
            transaction.setIdCompte(compteId);
            transaction.setIdTypeTransaction(2); // Type 2 = Retrait
            transaction.setMontant(BigDecimal.valueOf(montant));
            
            // Utiliser la date fournie ou la date actuelle si non fournie
            if (dateTransaction != null) {
                transaction.setDateTransaction(dateTransaction);
            } else {
                transaction.setDateTransaction(LocalDateTime.now());
            }
            
            transactionService.createTransaction(transaction);
            
            // Retourner le compte mis à jour
            return compteDepotService.getCompteById(compteId);
        } catch (ServerApplicationException e) {
            throw new RuntimeException(exceptionHandlingService.getUserFriendlyMessage(e));
        }
    }
}
