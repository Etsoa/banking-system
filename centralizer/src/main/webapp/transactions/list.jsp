<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.centralizer.dto.Transaction" %>
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
    <h1>Banking System - Transactions</h1>
    <p>DEBUG: JSP chargé !</p>
    <%
        System.out.println("=== JSP list.jsp chargé ===");
        List<Transaction> transactions = (List<Transaction>) request.getAttribute("transactions");
        String titre = (String) request.getAttribute("titre");
        System.out.println("Titre: " + titre);
        System.out.println("Transactions: " + (transactions != null ? transactions.size() : "null"));
    %>
    
    <p><a href="${pageContext.request.contextPath}/home">Accueil</a> | 
       <a href="${pageContext.request.contextPath}/comptes">Comptes</a> | 
       <a href="${pageContext.request.contextPath}/logout">Déconnexion</a></p>
    <hr>
    
    <h2><%= titre != null ? titre : "Transactions" %></h2>
    
    <%
        // Afficher les messages de succès ou d'erreur depuis la session
        String successMessage = (String) session.getAttribute("successMessage");
        String errorMessage = (String) session.getAttribute("errorMessage");
        
        if (successMessage != null) {
            session.removeAttribute("successMessage");
    %>
        <p style="color: green; font-weight: bold;">✓ <%= successMessage %></p>
    <%
        }
        if (errorMessage != null) {
            session.removeAttribute("errorMessage");
    %>
        <p style="color: red; font-weight: bold;">✗ <%= errorMessage %></p>
    <%
        }
    %>
    
    <p><a href="${pageContext.request.contextPath}/transactions">Toutes</a> | 
       <a href="${pageContext.request.contextPath}/transactions/en-attente">En attente</a> |
    <hr>
    
    <p>DEBUG: Nombre de transactions = <%= transactions != null ? transactions.size() : "null" %></p>
    <%
        if (transactions != null && !transactions.isEmpty()) {
            System.out.println("Affichage du tableau avec " + transactions.size() + " transactions");
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
            System.out.println("Aucune transaction à afficher");
    %>
    <p><strong>Aucune transaction.</strong></p>
    <%
        }
    %>
</body>
</html>
