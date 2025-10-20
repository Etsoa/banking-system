package com.example.centralizer.servlets;

import com.example.centralizer.ejb.AuthenticationServiceImpl;
import com.example.centralizer.ejb.CompteCourantServiceImpl;
import com.example.centralizer.dto.CompteCourant;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

/**
 * Servlet pour gérer les comptes courants - utilise JSP
 */
@WebServlet(urlPatterns = {"/comptes", "/comptes/*"})
public class CompteCourantServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CompteCourantServlet.class.getName());

    @EJB
    private AuthenticationServiceImpl authenticationService;
    
    @EJB
    private CompteCourantServiceImpl compteCourantService;

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
        if (session == null || session.getAttribute("authenticated") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }

        if (!authenticationService.isAuthenticated()) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }

        return true;
    }

    private void listComptes(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        try {
            CompteCourant compte = compteCourantService.getCompteById(idCompte);
            if (compte != null) {
                req.setAttribute("compte", compte);
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
