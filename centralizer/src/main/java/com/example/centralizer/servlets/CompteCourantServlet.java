package com.example.centralizer.servlets;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

import com.example.centralizer.dto.CompteCourant;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet pour gérer les comptes courants - utilise JSP et session beans
 */
@WebServlet(urlPatterns = {"/comptes", "/comptes/*"})
public class CompteCourantServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CompteCourantServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Vérifier l'authentification
        if (!checkAuthentication(req, resp)) {
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
        // Vérifier l'authentification
        if (!checkAuthentication(req, resp)) {
            return;
        }

        String action = req.getParameter("action");
        
        if ("create".equals(action)) {
            createCompte(req, resp);
        } else if ("depot".equals(action)) {
            effectuerDepot(req, resp);
        } else if ("retrait".equals(action)) {
            effectuerRetrait(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action invalide");
        }
    }

    private boolean checkAuthentication(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("authenticated") == null || 
            !(Boolean) session.getAttribute("authenticated")) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        return true;
    }

    private void listComptes(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        com.example.centralizer.ejb.CompteCourantServiceImpl compteCourantService = 
            (com.example.centralizer.ejb.CompteCourantServiceImpl) session.getAttribute("compteCourantService");
        
        if (compteCourantService == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Service non disponible - veuillez vous reconnecter");
            return;
        }
        
        try {
            List<CompteCourant> comptes = compteCourantService.getAllComptes();
            req.setAttribute("comptes", comptes);
            req.getRequestDispatcher("/comptes-courant/list.jsp").forward(req, resp);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des comptes: " + e.getMessage());
            req.setAttribute("error", "Erreur lors de la récupération des comptes");
            req.getRequestDispatcher("/comptes-courant/list.jsp").forward(req, resp);
        }
    }

    private void showCompteDetails(HttpServletRequest req, HttpServletResponse resp, Integer idCompte) throws ServletException, IOException {
        HttpSession session = req.getSession();
        com.example.centralizer.ejb.CompteCourantServiceImpl compteCourantService = 
            (com.example.centralizer.ejb.CompteCourantServiceImpl) session.getAttribute("compteCourantService");
        
        if (compteCourantService == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Service non disponible - veuillez vous reconnecter");
            return;
        }
        
        try {
            CompteCourant compte = compteCourantService.getCompteById(idCompte);
            if (compte != null) {
                req.setAttribute("compte", compte);
                
                // Récupérer les transactions du compte
                List<com.example.centralizer.dto.Transaction> transactions = 
                    compteCourantService.getTransactionsByCompte(idCompte);
                req.setAttribute("transactions", transactions);
                
                // Récupérer les devises disponibles pour les dépôts/retraits
                LOGGER.info("=== DEBUT récupération des devises ===");
                com.example.centralizer.ejb.EchangeServiceImpl echangeService = 
                    (com.example.centralizer.ejb.EchangeServiceImpl) session.getAttribute("echangeService");
                
                LOGGER.info("EchangeService depuis session: " + (echangeService != null ? "EXISTE" : "NULL"));
                
                if (echangeService != null) {
                    try {
                        LOGGER.info("Appel de getEchangesActifs()...");
                        List<com.example.centralizer.dto.echange.Echange> devises = echangeService.getEchangesActifs();
                        LOGGER.info("Devises récupérées: " + (devises != null ? devises.size() + " devises" : "NULL"));
                        
                        if (devises != null) {
                            for (com.example.centralizer.dto.echange.Echange dev : devises) {
                                LOGGER.info("  - " + dev.getNom() + " = " + dev.getValeur() + " MGA");
                            }
                        }
                        
                        req.setAttribute("devises", devises);
                        LOGGER.info("Devises ajoutées à l'attribut de requête");
                    } catch (Exception e) {
                        LOGGER.severe("Erreur lors de la récupération des devises: " + e.getClass().getName());
                        LOGGER.severe("Message: " + e.getMessage());
                        e.printStackTrace();
                        // Continuer sans les devises - seul MGA sera disponible
                    }
                } else {
                    LOGGER.warning("EchangeService non disponible dans la session - seul MGA sera disponible");
                }
                
                LOGGER.info("=== FIN récupération des devises ===");
                
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
        HttpSession session = req.getSession();
        com.example.centralizer.ejb.CompteCourantServiceImpl compteCourantService = 
            (com.example.centralizer.ejb.CompteCourantServiceImpl) session.getAttribute("compteCourantService");
        
        if (compteCourantService == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Service non disponible - veuillez vous reconnecter");
            return;
        }
        
        try {
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
        HttpSession session = req.getSession();
        com.example.centralizer.ejb.CompteCourantServiceImpl compteCourantService = 
            (com.example.centralizer.ejb.CompteCourantServiceImpl) session.getAttribute("compteCourantService");
        
        if (compteCourantService == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Service non disponible - veuillez vous reconnecter");
            return;
        }
        
        try {
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
        HttpSession session = req.getSession();
        com.example.centralizer.ejb.CompteCourantServiceImpl compteCourantService = 
            (com.example.centralizer.ejb.CompteCourantServiceImpl) session.getAttribute("compteCourantService");
        
        if (compteCourantService == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Service non disponible - veuillez vous reconnecter");
            return;
        }
        
        try {
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
