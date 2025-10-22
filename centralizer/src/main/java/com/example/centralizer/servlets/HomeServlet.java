package com.example.centralizer.servlets;

import com.example.centralizer.ejb.AuthenticationServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Servlet pour la page d'accueil - utilise JSP et session beans
 */
@WebServlet(urlPatterns = {"/", "/home"})
public class HomeServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(HomeServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Vérifier l'authentification via la session HTTP
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("authenticated") == null || 
            !(Boolean) session.getAttribute("authenticated")) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Récupérer le username depuis la session
        String username = (String) session.getAttribute("username");
        req.setAttribute("username", username);
        
        // Transférer vers la page JSP d'accueil
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
