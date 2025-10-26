package com.example.centralizer.servlets;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtre pour protéger l'accès aux ressources authentifiées
 * Redirige les utilisateurs non authentifiés vers la page de login
 */
@WebFilter(urlPatterns = {"/home", "/comptes/*", "/transactions/*", "/echanges/*"})
public class AuthenticationFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialisation du filtre
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Vérifier si l'utilisateur est authentifié
        if (!SessionManager.isAuthenticated(httpRequest)) {
            // Rediriger vers la page de login
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }
        
        // Continuer avec la requête
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // Nettoyage du filtre
    }
}
