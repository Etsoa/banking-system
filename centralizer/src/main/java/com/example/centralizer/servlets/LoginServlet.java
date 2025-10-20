package com.example.centralizer.servlets;

import com.example.centralizer.dto.LoginRequest;
import com.example.centralizer.dto.LoginResponse;
import com.example.centralizer.ejb.AuthenticationServiceImpl;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Servlet de login pour authentifier les utilisateurs - utilise JSP
 */
@WebServlet(urlPatterns = {"/login", "/logout"})
public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());

    @EJB
    private AuthenticationServiceImpl authenticationService;

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
            // Créer la requête de login
            LoginRequest loginRequest = new LoginRequest(username, password);
            
            // Authentification via EJB
            LoginResponse loginResponse = authenticationService.login(loginRequest);
            
            if (loginResponse.isSuccess()) {
                // Créer la session HTTP
                HttpSession session = req.getSession(true);
                session.setAttribute("username", username);
                session.setAttribute("userId", loginResponse.getIdUtilisateur());
                session.setAttribute("authenticated", true);
                
                LOGGER.info("Login réussi pour: " + username);
                
                // Rediriger vers la page d'accueil
                resp.sendRedirect(req.getContextPath() + "/home");
            } else {
                req.setAttribute("error", loginResponse.getMessage());
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de l'authentification: " + e.getMessage());
            req.setAttribute("error", "Erreur de connexion au serveur");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            authenticationService.logout();
        } catch (Exception e) {
            LOGGER.warning("Erreur lors de la déconnexion: " + e.getMessage());
        }
        
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
