package com.example.centralizer.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.centralizer.exceptions.ServerApplicationException;
import com.example.centralizer.models.Client;
import com.example.centralizer.models.compteCourantDTO.CompteCourant;
import com.example.centralizer.models.compteCourantDTO.CompteCourantAvecStatut;
import com.example.centralizer.models.compteCourantDTO.Transaction;
import com.example.centralizer.models.compteCourantDTO.TypeTransaction;
import com.example.centralizer.models.compteCourantDTO.Transfert;
import com.example.centralizer.services.ClientService;
import com.example.centralizer.services.CompteCourantService;
import com.example.centralizer.services.TransactionCompteCourantService;
import com.example.centralizer.services.ExceptionHandlingService;

@Controller
public class CompteCourantController {
    
    @Autowired
    private ClientService clientService;

    @Autowired
    private CompteCourantService compteCourantService;

    @Autowired
    private TransactionCompteCourantService transactionService;

    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    @GetMapping("/comptes-courant")
    public String getComptesCourant(Model model) {
        try {
            List<CompteCourantAvecStatut> comptesCourant = compteCourantService.getAllComptesAvecStatut();
            model.addAttribute("comptesCourant", comptesCourant);
            
            // Récupérer tous les clients pour afficher leurs noms
            List<Client> clients = clientService.getAllClients();
            Map<Integer, Client> clientsMap = new HashMap<>();
            for (Client client : clients) {
                clientsMap.put(client.getId(), client);
            }
            model.addAttribute("clientsMap", clientsMap);
            
        } catch (ServerApplicationException e) {
            // Gestion spécifique des erreurs de serveur
            String userMessage = exceptionHandlingService.getUserFriendlyMessage(e);
            model.addAttribute("comptesCourant", null);
            model.addAttribute("error", userMessage);
            model.addAttribute("errorDetails", e.getFormattedMessage());
        } catch (Exception e) {
            // Autres erreurs
            model.addAttribute("comptesCourant", null);
            model.addAttribute("error", "Erreur lors de la récupération des comptes courant: " + e.getMessage());
        }
        return "comptes-courant/list";
    }

    @GetMapping("/comptes-courant/new")
    public String showCreateCompteCourantForm(Model model) {
        try {
            // Récupérer la liste des clients pour la liste déroulante
            List<Client> clients = clientService.getAllClients();
            model.addAttribute("clients", clients);
            model.addAttribute("compteCourant", new CompteCourant());
            return "comptes-courant/compte-form";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des clients: " + e.getMessage());
            return "redirect:/comptes-courant";
        }
    }

    @PostMapping("/comptes-courant")
    public String createCompteCourant(@ModelAttribute("compteCourant") CompteCourant compteCourant, 
                                    Model model, 
                                    RedirectAttributes redirectAttributes) {
        try {
            // Définir la date d'ouverture au moment actuel
            compteCourant.setDateOuverture(LocalDateTime.now());
            
            // Créer le compte via le nouveau service avec gestion d'exceptions
            CompteCourant nouveauCompte = compteCourantService.createCompte(compteCourant);
            
            if (nouveauCompte != null) {
                redirectAttributes.addFlashAttribute("success", "Compte courant créé avec succès !");
                return "redirect:/comptes-courant";
            } else {
                model.addAttribute("error", "Erreur lors de la création du compte courant");
                model.addAttribute("clients", clientService.getAllClients());
                return "comptes-courant/compte-form";
            }
        } catch (ServerApplicationException e) {
            // Gestion spécifique des erreurs de serveur
            String userMessage = exceptionHandlingService.getUserFriendlyMessage(e);
            model.addAttribute("error", userMessage);
            model.addAttribute("errorDetails", e.getFormattedMessage());
            try {
                model.addAttribute("clients", clientService.getAllClients());
            } catch (Exception clientEx) {
                model.addAttribute("clients", null);
            }
            return "comptes-courant/compte-form";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la création du compte courant: " + e.getMessage());
            try {
                model.addAttribute("clients", clientService.getAllClients());
            } catch (Exception clientEx) {
                model.addAttribute("clients", null);
            }
            return "comptes-courant/compte-form";
        }
    }

    @GetMapping("/comptes-courant/details")
    @ResponseBody
    public CompteCourant getCompteCourantById(@RequestParam String id) {
        try {
            return compteCourantService.getCompteById(id);
        } catch (ServerApplicationException e) {
            // Pour les endpoints REST, on peut retourner null ou lever l'exception
            throw new RuntimeException(exceptionHandlingService.getUserFriendlyMessage(e));
        }
    }

    @GetMapping("/comptes-courant/transactions")
    public String getTransactionsByCompte(@RequestParam String compteId, 
                                        @RequestParam(required = false) Integer typeId,
                                        Model model) {
        try {
            // Récupérer le compte
            CompteCourant compte = compteCourantService.getCompteById(compteId);
            model.addAttribute("compte", compte);
            
            // Récupérer le client
            Client client = clientService.getClientById(compte.getIdClient());
            model.addAttribute("client", client);
            
            // Récupérer les transactions (filtrées ou non)
            List<Transaction> transactions;
            if (typeId != null && typeId > 0) {
                transactions = transactionService.getTransactionsByCompteAndType(compteId, typeId);
            } else {
                transactions = transactionService.getTransactionsByCompte(compteId);
            }
            model.addAttribute("transactions", transactions);
            
            // Récupérer tous les types de transaction pour le filtre
            List<TypeTransaction> typesTransaction = transactionService.getAllTypesTransaction();
            model.addAttribute("typesTransaction", typesTransaction);
            
            // Créer une map pour les libellés des types
            Map<Integer, String> typesMap = new HashMap<>();
            if (typesTransaction != null) {
                for (TypeTransaction type : typesTransaction) {
                    typesMap.put(type.getIdTypeTransaction(), type.getLibelle());
                }
            }
            model.addAttribute("typesMap", typesMap);
            
            // Type sélectionné pour le filtre
            model.addAttribute("selectedTypeId", typeId);
            
        } catch (ServerApplicationException e) {
            String userMessage = exceptionHandlingService.getUserFriendlyMessage(e);
            model.addAttribute("transactions", null);
            model.addAttribute("error", userMessage);
            model.addAttribute("errorDetails", e.getFormattedMessage());
        } catch (Exception e) {
            model.addAttribute("transactions", null);
            model.addAttribute("error", "Erreur lors de la récupération des transactions: " + e.getMessage());
        }
        return "comptes-courant/transactions";
    }

    @PostMapping("/comptes-courant/transactions/add")
    public String addTransaction(
            @RequestParam String compteId,
            @RequestParam int typeTransaction,
            @RequestParam double montant,
            RedirectAttributes redirectAttributes) {
        try {
            // Créer l'objet transaction
            Transaction transaction = new Transaction();
            transaction.setIdCompte(compteId);
            transaction.setIdTypeTransaction(typeTransaction);
            transaction.setMontant(BigDecimal.valueOf(montant));
            transaction.setDateTransaction(LocalDateTime.now());

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
        
        return "redirect:/comptes-courant/transactions?compteId=" + compteId;
    }

    @GetMapping("/comptes-courant/transferts")
    public String showTransfertForm(@RequestParam String compteId, Model model) {
        try {
            // Récupérer le compte envoyeur
            CompteCourant compte = compteCourantService.getCompteById(compteId);
            model.addAttribute("compteEnvoyeur", compte);
            
            // Récupérer tous les comptes pour le choix du destinataire
            List<CompteCourantAvecStatut> comptes = compteCourantService.getAllComptesAvecStatut();
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
        
        return "comptes-courant/transfert-form";
    }

    @PostMapping("/comptes-courant/transferts/add")
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
        
        return "redirect:/comptes-courant/transactions?compteId=" + compteEnvoyeur;
    }

    @GetMapping("/comptes-courant/transferts/list")
    public String getTransferts(@RequestParam(required = false) String compteId, Model model) {
        try {
            List<Transfert> transferts;
            if (compteId != null && !compteId.trim().isEmpty()) {
                transferts = transactionService.getTransfertsByCompte(compteId);
                CompteCourant compte = compteCourantService.getCompteById(compteId);
                model.addAttribute("compte", compte);
            } else {
                transferts = transactionService.getAllTransferts();
            }
            model.addAttribute("transferts", transferts);
            
            // Récupérer tous les comptes pour l'affichage
            List<CompteCourantAvecStatut> comptes = compteCourantService.getAllComptesAvecStatut();
            Map<String, CompteCourantAvecStatut> comptesMap = new HashMap<>();
            for (CompteCourantAvecStatut compte : comptes) {
                comptesMap.put(compte.getIdCompte(), compte);
            }
            model.addAttribute("comptesMap", comptesMap);
            
            // Récupérer tous les clients pour afficher leurs noms
            List<Client> clients = clientService.getAllClients();
            Map<Integer, Client> clientsMap = new HashMap<>();
            for (Client client : clients) {
                clientsMap.put(client.getId(), client);
            }
            model.addAttribute("clientsMap", clientsMap);
            
        } catch (Exception e) {
            model.addAttribute("transferts", null);
            model.addAttribute("error", "Erreur lors de la récupération des transferts: " + e.getMessage());
        }
        
        return "comptes-courant/transferts";
    }
}
