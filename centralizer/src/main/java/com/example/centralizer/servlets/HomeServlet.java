package com.example.centralizer.servlets;

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
 * Servlet pour la page d'accueil - utilise JSP
 */
@WebServlet(urlPatterns = {"/", "/home"})
public class HomeServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(HomeServlet.class.getName());

    @EJB
    private AuthenticationServiceImpl authenticationService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Vérifier l'authentification
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("authenticated") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Vérifier que l'utilisateur est toujours authentifié via EJB
        if (!authenticationService.isAuthenticated()) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Transférer vers la page JSP d'accueil
        String username = (String) session.getAttribute("username");
        req.setAttribute("username", username);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
