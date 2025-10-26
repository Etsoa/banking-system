<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.centralizer.dto.comptecourant.Transaction" %>
<%@ page import="com.example.centralizer.dto.comptecourant.SessionUtilisateur" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Transactions</title>
</head>
<body>
    <nav class="navbar">
        <div class="container">
            <h1>Banking System Centralizer</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/home">Accueil</a>
                <a href="${pageContext.request.contextPath}/comptes">Comptes</a>
                <%
                    SessionUtilisateur sessionUtilisateur = (SessionUtilisateur) request.getAttribute("sessionUtilisateur");
                    if (sessionUtilisateur != null) {
                %>
                    <span>Utilisateur: <strong><%= sessionUtilisateur.getNomUtilisateur() %></strong></span>
                <%
                    }
                %>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary">Déconnexion</a>
            </div>
        </div>
    </nav>
    
    <div class="container">
        <h1>Banking System - Transactions</h1>
    
    <%
        // Afficher les messages de session
        String successMessage = (String) session.getAttribute("successMessage");
        String errorMessage = (String) session.getAttribute("errorMessage");
        
        if (successMessage != null) {
            session.removeAttribute("successMessage");
    %>
        <div class="alert alert-success">
            <%= successMessage %>
        </div>
    <%
        }
        
        if (errorMessage != null) {
            session.removeAttribute("errorMessage");
    %>
        <div class="alert alert-error">
            <%= errorMessage %>
        </div>
    <%
        }
    %>
    
    <%
        List<Transaction> transactions = (List<Transaction>) request.getAttribute("transactions");
        String titre = (String) request.getAttribute("titre");
    %>
    
    <h2><%= titre != null ? titre : "Transactions" %></h2>
    
    <%
        if (transactions != null && !transactions.isEmpty()) {
    %>
    <table border="1" cellpadding="5" cellspacing="0">
        <tr>
            <th>ID</th>
            <th>Date</th>
            <th>Type</th>
            <th>Montant</th>
            <th>Compte</th>
            <th>Statut</th>
            <th>Actions</th>
        </tr>
        <%
            for (Transaction t : transactions) {
        %>
        <tr>
            <td><%= t.getIdTransaction() %></td>
            <td><%= t.getDateTransaction() %></td>
            <td><%= t.getTypeTransaction() %></td>
            <td><%= t.getMontant() %></td>
            <td><%= t.getIdCompte() %></td>
            <td><%= t.getStatutTransaction() %></td>
            <td>
                <% if (t.getStatutTransaction() != null && "en_attente".equals(t.getStatutTransaction().name())) { %>
                    <form method="post" action="${pageContext.request.contextPath}/transactions" style="display:inline;">
                        <input type="hidden" name="action" value="valider">
                        <input type="hidden" name="idTransaction" value="<%= t.getIdTransaction() %>">
                        <button type="submit" style="background-color: green; color: white; padding: 5px 10px; border: none; cursor: pointer;"
                                onclick="return confirm('Confirmer la validation de cette transaction ?')">
                            ✓ Valider
                        </button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/transactions" style="display:inline;">
                        <input type="hidden" name="action" value="refuser">
                        <input type="hidden" name="idTransaction" value="<%= t.getIdTransaction() %>">
                        <button type="submit" style="background-color: red; color: white; padding: 5px 10px; border: none; cursor: pointer;"
                                onclick="return confirm('Confirmer le refus de cette transaction ?')">
                            ✗ Refuser
                        </button>
                    </form>
                <% } else { %>
                    -
                <% } %>
            </td>
        </tr>
        <%
            }
        %>
    </table>
    <%
        } else {
    %>
        <p><strong>Aucune transaction.</strong></p>
    <%
        }
    %>
    </div>
</body>
</html>