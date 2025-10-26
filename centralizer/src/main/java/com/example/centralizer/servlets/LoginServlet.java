package com.example.centralizer.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import com.example.centralizer.dto.comptecourant.LoginResponse;
import com.example.centralizer.dto.comptecourant.SessionUtilisateur;
import com.example.centralizer.ejb.AuthenticationServiceImpl;
import com.example.centralizer.ejb.CompteCourantServiceImpl;
import com.example.centralizer.ejb.EchangeServiceImpl;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet de login pour authentifier les utilisateurs
 * Stocke la SessionUtilisateur dans la session HTTP pour chaque requête
 */
@WebServlet(urlPatterns = {"/login", "/logout"})
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    
    private static final String SESSION_UTILISATEUR_KEY = "sessionUtilisateur";
    
    @EJB
    private AuthenticationServiceImpl authenticationService;
    
    @EJB
    private CompteCourantServiceImpl compteCourantService;
    
    @EJB
    private EchangeServiceImpl echangeService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        
        if ("/logout".equals(path)) {
            handleLogout(req, resp);
        } else {
            // Afficher la page de login JSP
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            req.setAttribute("error", "Nom d'utilisateur et mot de passe requis");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        try {
            // Authentifier l'utilisateur
            LoginResponse loginResponse = compteCourantService.login(username, password);
            
            if (!loginResponse.isSuccess()) {
                req.setAttribute("error", "Nom d'utilisateur ou mot de passe invalide");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }
            
            // Récupérer la session utilisateur complète depuis le service d'authentification
            SessionUtilisateur sessionUtilisateur = compteCourantService.getSessionUtilisateur();
            
            if (sessionUtilisateur == null) {
                req.setAttribute("error", "Impossible de créer la session utilisateur");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }
            
            // Créer la session HTTP et stocker la SessionUtilisateur
            HttpSession httpSession = req.getSession(true);
            httpSession.setAttribute(SESSION_UTILISATEUR_KEY, sessionUtilisateur);
            httpSession.setAttribute("echangeService", echangeService);
            
            LOGGER.info("Login réussi pour: " + sessionUtilisateur.getNomUtilisateur() + 
                       " (ID: " + sessionUtilisateur.getIdUtilisateur() + 
                       ", Rôle: " + sessionUtilisateur.getRoleUtilisateur() + ")");
            
            // Rediriger vers la page d'accueil
            resp.sendRedirect(req.getContextPath() + "/home");
            
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de l'authentification: " + e.getMessage());
            req.setAttribute("error", "Erreur de connexion au serveur");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            try {
                // Récupérer la SessionUtilisateur pour logging
                SessionUtilisateur sessionUtilisateur = 
                    (SessionUtilisateur) session.getAttribute(SESSION_UTILISATEUR_KEY);
                
                if (sessionUtilisateur != null) {
                    LOGGER.info("Déconnexion de: " + sessionUtilisateur.getNomUtilisateur());
                }
                
            } catch (Exception e) {
                LOGGER.warning("Erreur lors de la déconnexion: " + e.getMessage());
            }
            
            // Invalider la session HTTP
            session.invalidate();
        }
        
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
