package com.example.centralizer.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.example.centralizer.dto.LoginRequest;
import com.example.centralizer.dto.LoginResponse;
import com.example.centralizer.ejb.AuthenticationServiceImpl;
import com.example.centralizer.ejb.CompteCourantServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet de login pour authentifier les utilisateurs - utilise JSP et JNDI lookup
 */
@WebServlet(urlPatterns = {"/login", "/logout"})
public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    
    private static final String AUTH_SERVICE_JNDI = "java:module/AuthenticationServiceImpl";
    private static final String COMPTE_COURANT_SERVICE_JNDI = "java:module/CompteCourantServiceImpl";
    
    /**
     * Obtenir une nouvelle instance d'AuthenticationService via JNDI lookup
     */
    private AuthenticationServiceImpl getAuthenticationService() throws NamingException {
        InitialContext ctx = new InitialContext();
        return (AuthenticationServiceImpl) ctx.lookup(AUTH_SERVICE_JNDI);
    }
    
    /**
     * Obtenir une nouvelle instance de CompteCourantService via JNDI lookup
     */
    private CompteCourantServiceImpl getCompteCourantService() throws NamingException {
        InitialContext ctx = new InitialContext();
        return (CompteCourantServiceImpl) ctx.lookup(COMPTE_COURANT_SERVICE_JNDI);
    }

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
            // Obtenir une nouvelle instance du service d'authentification via JNDI
            AuthenticationServiceImpl authService = getAuthenticationService();
            
            // Obtenir une nouvelle instance du service CompteCourant via JNDI
            CompteCourantServiceImpl compteCourantService = getCompteCourantService();
            
            // Authentifier d'abord sur le serveur backend
            boolean backendLoginSuccess = compteCourantService.login(username, password);
            
            if (!backendLoginSuccess) {
                req.setAttribute("error", "Nom d'utilisateur ou mot de passe invalide");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }
            
            // Récupérer les informations réelles de l'utilisateur depuis le serveur
            LoginResponse currentUserInfo = compteCourantService.getCurrentUser();
            Integer realUserId = null;
            String realUsername = username;
            
            if (currentUserInfo != null && currentUserInfo.isSuccess()) {
                realUserId = currentUserInfo.getIdUtilisateur();
                realUsername = currentUserInfo.getNomUtilisateur();
                LOGGER.info("Informations utilisateur récupérées du serveur: ID=" + realUserId + ", Username=" + realUsername);
            } else {
                LOGGER.warning("Impossible de récupérer les informations utilisateur, utilisation de valeurs par défaut");
                realUserId = 1; // Valeur par défaut si la récupération échoue
            }
            
            // Créer la requête de login pour la session locale avec l'ID réel
            LoginRequest loginRequest = new LoginRequest(realUsername, password, realUserId);
            
            // Créer la session locale
            LoginResponse loginResponse = authService.login(loginRequest);
            
            if (loginResponse.isSuccess()) {
                // Créer la session HTTP et stocker les beans EJB dans la session
                HttpSession session = req.getSession(true);
                session.setAttribute("username", realUsername);
                session.setAttribute("userId", realUserId);
                session.setAttribute("authenticated", true);
                session.setAttribute("authService", authService); // Stocker l'EJB d'authentification dans la session
                session.setAttribute("compteCourantService", compteCourantService); // Stocker l'EJB de compte courant dans la session
                
                LOGGER.info("Login réussi pour: " + realUsername + " (ID: " + realUserId + ") - Services EJB initialisés et authentifiés sur le serveur backend");
                
                // Rediriger vers la page d'accueil
                resp.sendRedirect(req.getContextPath() + "/home");
            } else {
                req.setAttribute("error", loginResponse.getMessage());
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de l'authentification: " + e.getMessage());
            e.printStackTrace();
            req.setAttribute("error", "Erreur de connexion au serveur");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            try {
                // Récupérer les services de la session et les nettoyer
                AuthenticationServiceImpl authService = 
                    (AuthenticationServiceImpl) session.getAttribute("authService");
                if (authService != null) {
                    authService.logout();
                }
                
                // Déconnecter du serveur backend
                CompteCourantServiceImpl compteCourantService =
                    (CompteCourantServiceImpl) session.getAttribute("compteCourantService");
                if (compteCourantService != null) {
                    compteCourantService.logout();
                }
                
            } catch (Exception e) {
                LOGGER.warning("Erreur lors de la déconnexion: " + e.getMessage());
            }
            session.invalidate();
        }
        
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
