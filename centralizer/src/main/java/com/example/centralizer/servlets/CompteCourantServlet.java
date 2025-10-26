package com.example.centralizer.servlets;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

import com.example.centralizer.dto.comptecourant.CompteCourant;
import com.example.centralizer.dto.comptecourant.SessionUtilisateur;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Servlet pour gérer les comptes courants - utilise SessionManager et session HTTP
 */
@WebServlet(urlPatterns = {"/comptes", "/comptes/*"})
public class CompteCourantServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CompteCourantServlet.class.getName());
    
    private static final String COMPTE_COURANT_SERVICE_JNDI = "java:module/CompteCourantServiceImpl";
    
    /**
     * Obtenir une nouvelle instance de CompteCourantService via JNDI lookup
     */
    private com.example.centralizer.ejb.CompteCourantServiceImpl getCompteCourantService() throws NamingException {
        InitialContext ctx = new InitialContext();
        return (com.example.centralizer.ejb.CompteCourantServiceImpl) ctx.lookup(COMPTE_COURANT_SERVICE_JNDI);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Vérifier l'authentification via SessionManager
        if (!SessionManager.isAuthenticated(req)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || "/".equals(pathInfo)) {
            // Liste des comptes
            listComptes(req, resp);
        } else {
            // Détails d'un compte
            String[] parts = pathInfo.split("/");
            if (parts.length > 1) {
                try {
                    Integer idCompte = Integer.parseInt(parts[1]);
                    showCompteDetails(req, resp, idCompte);
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de compte invalide");
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Vérifier l'authentification via SessionManager
        if (!SessionManager.isAuthenticated(req)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        
        switch (action != null ? action : "") {
            case "create":
                createCompte(req, resp);
                break;
            case "depot":
                effectuerDepot(req, resp);
                break;
            case "retrait":
                effectuerRetrait(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action invalide");
        }
    }

    private void listComptes(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            com.example.centralizer.ejb.CompteCourantServiceImpl compteCourantService = getCompteCourantService();
            
            List<CompteCourant> comptes = compteCourantService.getAllComptes();
            req.setAttribute("comptes", comptes);
            
            // Ajouter les infos de session
            SessionUtilisateur session = SessionManager.getSessionUtilisateur(req);
            req.setAttribute("sessionUtilisateur", session);
            
            req.getRequestDispatcher("/comptes-courant/list.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des comptes: " + e.getMessage());
            req.setAttribute("error", "Erreur lors de la récupération des comptes");
            try {
                req.getRequestDispatcher("/comptes-courant/list.jsp").forward(req, resp);
            } catch (Exception e2) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur");
            }
        }
    }

    private void showCompteDetails(HttpServletRequest req, HttpServletResponse resp, Integer idCompte) throws ServletException, IOException {
        try {
            com.example.centralizer.ejb.CompteCourantServiceImpl compteCourantService = getCompteCourantService();
            
            CompteCourant compte = compteCourantService.getCompteById(idCompte);
            if (compte != null) {
                req.setAttribute("compte", compte);
                
                // Récupérer les transactions du compte
                List<com.example.centralizer.dto.comptecourant.Transaction> transactions = 
                    compteCourantService.getTransactionsByCompte(idCompte);
                req.setAttribute("transactions", transactions);
                
                // Récupérer le service Echange de la session
                HttpSession httpSession = req.getSession(false);
                if (httpSession != null) {
                    com.example.centralizer.ejb.EchangeServiceImpl echangeService = 
                        (com.example.centralizer.ejb.EchangeServiceImpl) httpSession.getAttribute("echangeService");
                    
                    if (echangeService != null) {
                        try {
                            // Récupérer les devises actives pour aujourd'hui
                            List<com.example.centralizer.dto.echange.Echange> devises = echangeService.getEchangesActifs(java.time.LocalDate.now());
                            req.setAttribute("devises", devises);
                        } catch (Exception e) {
                            LOGGER.warning("Erreur lors de la récupération des devises: " + e.getMessage());
                            // Continuer sans les devises - seul MGA sera disponible
                        }
                    }
                }
                
                // Ajouter les infos de session
                SessionUtilisateur session = SessionManager.getSessionUtilisateur(req);
                req.setAttribute("sessionUtilisateur", session);
                
                req.getRequestDispatcher("/comptes-courant/details.jsp").forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Compte non trouvé");
            }
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération du compte: " + e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur");
        }
    }

    private void createCompte(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            com.example.centralizer.ejb.CompteCourantServiceImpl compteCourantService = getCompteCourantService();
            
            String soldeStr = req.getParameter("solde");
            BigDecimal solde = new BigDecimal(soldeStr != null ? soldeStr : "0");
            
            CompteCourant compte = compteCourantService.createCompte(solde);
            if (compte != null) {
                resp.sendRedirect(req.getContextPath() + "/comptes");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la création du compte");
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Montant invalide");
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la création du compte: " + e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur");
        }
    }

    private void effectuerDepot(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            com.example.centralizer.ejb.CompteCourantServiceImpl compteCourantService = getCompteCourantService();
            
            Integer idCompte = Integer.parseInt(req.getParameter("idCompte"));
            BigDecimal montant = new BigDecimal(req.getParameter("montant"));
            
            compteCourantService.depot(idCompte, montant);
            resp.sendRedirect(req.getContextPath() + "/comptes/" + idCompte);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres invalides");
        } catch (Exception e) {
            LOGGER.severe("Erreur lors du dépôt: " + e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur");
        }
    }

    private void effectuerRetrait(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            com.example.centralizer.ejb.CompteCourantServiceImpl compteCourantService = getCompteCourantService();
            
            Integer idCompte = Integer.parseInt(req.getParameter("idCompte"));
            BigDecimal montant = new BigDecimal(req.getParameter("montant"));
            
            compteCourantService.retrait(idCompte, montant);
            resp.sendRedirect(req.getContextPath() + "/comptes/" + idCompte);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres invalides");
        } catch (Exception e) {
            LOGGER.severe("Erreur lors du retrait: " + e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur");
        }
    }
}
