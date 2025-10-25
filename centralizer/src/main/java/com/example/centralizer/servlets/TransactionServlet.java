package com.example.centralizer.servlets;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

import com.example.centralizer.dto.Transaction;
import com.example.centralizer.dto.echange.Echange;
import com.example.centralizer.ejb.CompteCourantServiceImpl;
import com.example.centralizer.ejb.EchangeServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet pour la gestion des transactions
 */
@WebServlet(urlPatterns = {"/transactions", "/transactions/en-attente", "/transactions/compte/*", "/transactions/depot", "/transactions/retrait"})
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
            } else if (pathInfo.equals("/depot") || pathInfo.equals("/retrait")) {
                // /transactions/depot ou /transactions/retrait
                afficherFormulaireTransaction(req, resp, session, pathInfo.substring(1));
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
        
        LOGGER.info("Action reçue: " + action);
        
        if (action == null) {
            LOGGER.warning("Action null - redirection");
            resp.sendRedirect(req.getContextPath() + "/transactions");
            return;
        }
        
        try {
            switch (action) {
                case "valider":
                case "refuser":
                    traiterValidationRefus(req, resp, service, session, action);
                    break;
                    
                case "depot":
                case "retrait":
                    traiterDepotRetrait(req, resp, service, session, action);
                    break;
                    
                default:
                    LOGGER.warning("Action inconnue: " + action);
                    session.setAttribute("errorMessage", "Action inconnue");
                    resp.sendRedirect(req.getContextPath() + "/transactions");
            }
            
        } catch (Exception e) {
            LOGGER.severe("Erreur inattendue dans doPost: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "Erreur technique: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/transactions");
        }
    }
    
    private void traiterValidationRefus(HttpServletRequest req, HttpServletResponse resp,
                                       CompteCourantServiceImpl service, HttpSession session,
                                       String action) throws IOException {
        String idTransactionStr = req.getParameter("idTransaction");
        
        LOGGER.info("ID Transaction: " + idTransactionStr);
        
        if (idTransactionStr == null) {
            LOGGER.warning("ID transaction null - redirection");
            resp.sendRedirect(req.getContextPath() + "/transactions");
            return;
        }
        
        try {
            Integer idTransaction = Integer.parseInt(idTransactionStr);
            LOGGER.info("ID Transaction parsé: " + idTransaction);
            
            boolean success = false;
            String message = "";
            
            if ("valider".equals(action)) {
                LOGGER.info("=== DEBUT validerTransaction pour ID: " + idTransaction + " ===");
                success = service.validerTransaction(idTransaction);
                LOGGER.info("Résultat validerTransaction: " + success);
                message = success ? "Transaction #" + idTransaction + " validée avec succès" 
                                 : "Erreur lors de la validation de la transaction";
            } else {
                LOGGER.info("=== DEBUT refuserTransaction pour ID: " + idTransaction + " ===");
                success = service.refuserTransaction(idTransaction);
                LOGGER.info("Résultat refuserTransaction: " + success);
                message = success ? "Transaction #" + idTransaction + " refusée avec succès" 
                                 : "Erreur lors du refus de la transaction";
            }
            
            LOGGER.info("Message final: " + message);
            LOGGER.info("Success: " + success);
            
            session.setAttribute(success ? "successMessage" : "errorMessage", message);
            LOGGER.info("Message stocké dans session avec clé: " + (success ? "successMessage" : "errorMessage"));
            resp.sendRedirect(req.getContextPath() + "/transactions");
            
        } catch (NumberFormatException e) {
            LOGGER.severe("ID transaction invalide: " + idTransactionStr);
            resp.sendRedirect(req.getContextPath() + "/transactions");
        }
    }
    
    private void traiterDepotRetrait(HttpServletRequest req, HttpServletResponse resp,
                                     CompteCourantServiceImpl service, HttpSession session,
                                     String action) throws IOException {
        String idCompteStr = req.getParameter("idCompte");
        String montantStr = req.getParameter("montant");
        String devise = req.getParameter("devise");
        String dateTransactionStr = req.getParameter("dateTransaction");
        
        LOGGER.info("Traitement " + action + " - Compte: " + idCompteStr + ", Montant: " + montantStr + ", Devise: " + devise + ", Date: " + dateTransactionStr);
        
        if (idCompteStr == null || montantStr == null || devise == null || dateTransactionStr == null) {
            session.setAttribute("errorMessage", "Paramètres manquants");
            resp.sendRedirect(req.getContextPath() + "/transactions/" + action);
            return;
        }
        
        try {
            Integer idCompte = Integer.parseInt(idCompteStr);
            BigDecimal montantDevise = new BigDecimal(montantStr);
            java.time.LocalDate dateTransaction = java.time.LocalDate.parse(dateTransactionStr);
            
            // Convertir le montant en Ariary si devise différente de MGA
            BigDecimal montantAriary = montantDevise;
            if (!"MGA".equals(devise)) {
                EchangeServiceImpl echangeService = (EchangeServiceImpl) session.getAttribute("echangeService");
                if (echangeService == null) {
                    LOGGER.warning("EchangeService non disponible dans la session - impossible de convertir");
                    session.setAttribute("errorMessage", "Service de conversion non disponible - veuillez vous reconnecter");
                    resp.sendRedirect(req.getContextPath() + "/transactions/" + action);
                    return;
                }
                
                // Utiliser la date de transaction pour trouver le taux actif à cette date
                montantAriary = echangeService.convertirVersAriaryADate(devise + "/MGA", montantDevise, dateTransaction);
                LOGGER.info("Montant converti à la date " + dateTransaction + ": " + montantDevise + " " + devise + " = " + montantAriary + " MGA");
            }
            
            boolean success = false;
            String message = "";
            
            if ("depot".equals(action)) {
                success = service.creerDepot(idCompte, montantAriary);
                message = success ? "Dépôt de " + montantDevise + " " + devise + " (" + montantAriary + " MGA) effectué avec succès à la date du " + dateTransaction 
                                 : "Erreur lors du dépôt";
            } else {
                success = service.creerRetrait(idCompte, montantAriary);
                message = success ? "Retrait de " + montantDevise + " " + devise + " (" + montantAriary + " MGA) effectué avec succès à la date du " + dateTransaction 
                                 : "Erreur lors du retrait";
            }
            
            session.setAttribute(success ? "successMessage" : "errorMessage", message);
            resp.sendRedirect(req.getContextPath() + "/comptes-courant/" + idCompte);
            
        } catch (NumberFormatException e) {
            LOGGER.severe("Paramètre invalide: " + e.getMessage());
            session.setAttribute("errorMessage", "Montant ou ID compte invalide");
            resp.sendRedirect(req.getContextPath() + "/transactions/" + action);
        } catch (java.time.format.DateTimeParseException e) {
            LOGGER.severe("Date invalide: " + e.getMessage());
            session.setAttribute("errorMessage", "Format de date invalide");
            resp.sendRedirect(req.getContextPath() + "/transactions/" + action);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la conversion ou transaction: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "Erreur: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/transactions/" + action);
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
    
    private void afficherFormulaireTransaction(HttpServletRequest req, HttpServletResponse resp,
                                              HttpSession session, String type) throws ServletException, IOException {
        // Récupérer la liste des devises disponibles
        EchangeServiceImpl echangeService = (EchangeServiceImpl) session.getAttribute("echangeService");
        if (echangeService != null) {
            try {
                List<Echange> devises = echangeService.getEchangesActifs();
                req.setAttribute("devises", devises);
            } catch (Exception e) {
                LOGGER.warning("Erreur lors de la récupération des devises: " + e.getMessage());
                // Continuer sans les devises
            }
        } else {
            LOGGER.warning("EchangeService non disponible dans la session");
        }
        
        req.setAttribute("type", type);
        
        // Récupérer la liste des comptes pour le dropdown
        CompteCourantServiceImpl compteCourantService = (CompteCourantServiceImpl) session.getAttribute("compteCourantService");
        req.setAttribute("comptes", compteCourantService.getAllComptes());
        
        req.getRequestDispatcher("/transactions/form.jsp").forward(req, resp);
    }
}
