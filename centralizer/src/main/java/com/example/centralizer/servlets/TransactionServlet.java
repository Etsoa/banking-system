package com.example.centralizer.servlets;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import com.example.centralizer.dto.Transaction;
import com.example.centralizer.ejb.CompteCourantServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet pour la gestion des transactions
 */
@WebServlet(urlPatterns = {"/transactions", "/transactions/en-attente", "/transactions/compte/*"})
public class TransactionServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(TransactionServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("=== TransactionServlet.doGet appelé ===");
        LOGGER.info("Request URI: " + req.getRequestURI());
        
        HttpSession session = req.getSession(false);
        
        if (session == null) {
            LOGGER.warning("Session is null - redirecting to login");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        CompteCourantServiceImpl service = (CompteCourantServiceImpl) session.getAttribute("compteCourantService");
        
        if (service == null) {
            LOGGER.warning("CompteCourantService not found in session - redirecting to login");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        String pathInfo = req.getPathInfo();
        
        try {
            if (pathInfo == null) {
                // /transactions
                afficherToutesTransactions(req, resp, service);
            } else if (pathInfo.equals("/en-attente")) {
                // /transactions/en-attente
                afficherTransactionsEnAttente(req, resp, service);
            } else if (pathInfo.startsWith("/compte/")) {
                // /transactions/compte/{id}
                String idStr = pathInfo.substring("/compte/".length());
                Integer idCompte = Integer.parseInt(idStr);
                afficherTransactionsCompte(req, resp, service, idCompte);
            }
        } catch (Exception e) {
            LOGGER.severe("Erreur lors du traitement de la requête: " + e.getMessage());
            req.setAttribute("error", "Erreur lors de la récupération des transactions");
            req.getRequestDispatcher("/transactions/list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("=== TransactionServlet.doPost appelé ===");
        
        HttpSession session = req.getSession(false);
        
        if (session == null || session.getAttribute("compteCourantService") == null) {
            LOGGER.warning("Session ou service null - redirection vers login");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        CompteCourantServiceImpl service = (CompteCourantServiceImpl) session.getAttribute("compteCourantService");
        
        String action = req.getParameter("action");
        String idTransactionStr = req.getParameter("idTransaction");
        
        LOGGER.info("Action reçue: " + action);
        LOGGER.info("ID Transaction: " + idTransactionStr);
        
        if (action == null || idTransactionStr == null) {
            LOGGER.warning("Action ou ID transaction null - redirection");
            resp.sendRedirect(req.getContextPath() + "/transactions");
            return;
        }
        
        try {
            Integer idTransaction = Integer.parseInt(idTransactionStr);
            LOGGER.info("ID Transaction parsé: " + idTransaction);
            
            boolean success = false;
            String message = "";
            
            switch (action) {
                case "valider":
                    LOGGER.info("=== DEBUT validerTransaction pour ID: " + idTransaction + " ===");
                    success = service.validerTransaction(idTransaction);
                    LOGGER.info("Résultat validerTransaction: " + success);
                    message = success ? "Transaction #" + idTransaction + " validée avec succès" 
                                     : "Erreur lors de la validation de la transaction";
                    break;
                    
                case "refuser":
                    LOGGER.info("=== DEBUT refuserTransaction pour ID: " + idTransaction + " ===");
                    success = service.refuserTransaction(idTransaction);
                    LOGGER.info("Résultat refuserTransaction: " + success);
                    message = success ? "Transaction #" + idTransaction + " refusée avec succès" 
                                     : "Erreur lors du refus de la transaction";
                    break;
                    
                default:
                    LOGGER.warning("Action inconnue: " + action);
                    message = "Action inconnue";
            }
            
            LOGGER.info("Message final: " + message);
            LOGGER.info("Success: " + success);
            
            // Rediriger vers la page d'origine avec un message
            session.setAttribute(success ? "successMessage" : "errorMessage", message);
            LOGGER.info("Message stocké dans session avec clé: " + (success ? "successMessage" : "errorMessage"));
            resp.sendRedirect(req.getContextPath() + "/transactions");
            
        } catch (NumberFormatException e) {
            LOGGER.severe("ID transaction invalide: " + idTransactionStr);
            resp.sendRedirect(req.getContextPath() + "/transactions");
        } catch (Exception e) {
            LOGGER.severe("Erreur inattendue dans doPost: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "Erreur technique: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/transactions");
        }
    }

    private void afficherToutesTransactions(HttpServletRequest req, HttpServletResponse resp, 
                                           CompteCourantServiceImpl service) throws ServletException, IOException {
        LOGGER.info("=== DEBUT afficherToutesTransactions ===");
        List<Transaction> transactions = service.getAllTransactions();
        LOGGER.info("Nombre de transactions récupérées: " + (transactions != null ? transactions.size() : "null"));
        req.setAttribute("transactions", transactions);
        req.setAttribute("titre", "Toutes les transactions");
        req.setAttribute("type", "all");
        LOGGER.info("Avant forward vers /transactions/list.jsp");
        req.getRequestDispatcher("/transactions/list.jsp").forward(req, resp);
        LOGGER.info("Après forward vers /transactions/list.jsp");
    }

    private void afficherTransactionsEnAttente(HttpServletRequest req, HttpServletResponse resp,
                                              CompteCourantServiceImpl service) throws ServletException, IOException {
        List<Transaction> transactions = service.getTransactionsEnAttente();
        LOGGER.info("Nombre de transactions en attente: " + (transactions != null ? transactions.size() : "null"));
        req.setAttribute("transactions", transactions);
        req.setAttribute("titre", "Transactions en attente de validation");
        req.setAttribute("type", "en-attente");
        req.getRequestDispatcher("/transactions/list.jsp").forward(req, resp);
    }

    private void afficherTransactionsCompte(HttpServletRequest req, HttpServletResponse resp,
                                           CompteCourantServiceImpl service, Integer idCompte) throws ServletException, IOException {
        List<Transaction> transactions = service.getTransactionsByCompte(idCompte);
        req.setAttribute("transactions", transactions);
        req.setAttribute("titre", "Transactions du compte #" + idCompte);
        req.setAttribute("type", "compte");
        req.setAttribute("idCompte", idCompte);
        req.getRequestDispatcher("/transactions/list.jsp").forward(req, resp);
    }
}
