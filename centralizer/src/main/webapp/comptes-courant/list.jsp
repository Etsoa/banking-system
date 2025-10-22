<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.centralizer.dto.CompteCourant" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Comptes Courants - Banking System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="container">
            <h1>Banking System Centralizer</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/home">Accueil</a>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary">Déconnexion</a>
            </div>
        </div>
    </nav>
    
    <div class="container">
        <h2>Liste des Comptes Courants</h2>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <div class="actions">
            <button onclick="document.getElementById('createModal').style.display='block'" class="btn btn-primary">
                Nouveau Compte
            </button>
        </div>
        
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID Compte</th>
                    <th>Solde</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%
                    List<CompteCourant> comptes = (List<CompteCourant>) request.getAttribute("comptes");
                    if (comptes != null && !comptes.isEmpty()) {
                        for (CompteCourant compte : comptes) {
                %>
                <tr>
                    <td><%= compte.getIdCompte() %></td>
                    <td><%= compte.getSolde() %> €</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/comptes/<%= compte.getIdCompte() %>" class="btn btn-sm">Détails</a>
                    </td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="3" style="text-align: center;">Aucun compte trouvé</td>
                </tr>
                <%
                    }
                %>
            </tbody>
        </table>
    </div>
    
    <!-- Modal Création Compte -->
    <div id="createModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="document.getElementById('createModal').style.display='none'">&times;</span>
            <h3>Créer un nouveau compte</h3>
            <form method="post" action="${pageContext.request.contextPath}/comptes">
                <input type="hidden" name="action" value="create">
                <div class="form-group">
                    <label for="solde">Solde initial:</label>
                    <input type="number" id="solde" name="solde" step="0.01" value="0" required>
                </div>
                <button type="submit" class="btn btn-primary">Créer</button>
            </form>
        </div>
    </div>
</body>
</html>
