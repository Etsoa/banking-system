<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.centralizer.dto.comptecourant.CompteCourant" %>
<%@ page import="com.example.centralizer.dto.comptecourant.SessionUtilisateur" %>
<%@ page import="com.example.centralizer.dto.echange.Echange" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= "depot".equals(request.getAttribute("type")) ? "Dépôt" : "Retrait" %> - Banking System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="container">
            <h1>Banking System Centralizer</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/home">Accueil</a>
                <a href="${pageContext.request.contextPath}/comptes">Comptes</a>
                <a href="${pageContext.request.contextPath}/transactions">Transactions</a>
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
        <%
            String type = (String) request.getAttribute("type");
            boolean isDepot = "depot".equals(type);
            
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
        <h2><%= isDepot ? "Effectuer un dépôt" : "Effectuer un retrait" %></h2>

        <div class="form-container">
            <form method="post" action="${pageContext.request.contextPath}/transactions">
                <input type="hidden" name="action" value="<%= type %>">

                <div class="form-group">
                    <label for="idCompte">Compte:</label>
                    <select id="idCompte" name="idCompte" required>
                        <option value="">Sélectionner un compte</option>
                        <%
                            List<CompteCourant> comptes = (List<CompteCourant>) request.getAttribute("comptes");
                            if (comptes != null) {
                                for (CompteCourant compte : comptes) {
                        %>
                            <option value="<%= compte.getIdCompte() %>">
                                Compte #<%= compte.getIdCompte() %> - Solde: <%= compte.getSolde() %> €
                            </option>
                        <%
                                }
                            }
                        %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="dateTransaction">Date de transaction:</label>
                    <input type="date" id="dateTransaction" name="dateTransaction"
                           value="<%= java.time.LocalDate.now() %>" required>
                </div>

                <div class="form-group">
                    <label for="devise">Devise:</label>
                    <select id="devise" name="devise" required>
                        <option value="MGA">MGA (Ariary malgache)</option>
                        <%
                            List<Echange> devises = (List<Echange>) request.getAttribute("devises");
                            if (devises != null) {
                                for (Echange echange : devises) {
                                    String code = echange.getNom().split("/")[0];
                        %>
                            <option value="<%= code %>">
                                <%= code %> (1 <%= code %> = <%= echange.getValeur() %> MGA)
                            </option>
                        <%
                                }
                            }
                        %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="montant">Montant:</label>
                    <input type="number" id="montant" name="montant" step="0.01" min="0.01" required>
                    <small>Le montant sera automatiquement converti en Ariary si vous choisissez une autre devise</small>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn <%= isDepot ? "btn-success" : "btn-warning" %>">
                        <%= isDepot ? "Effectuer le dépôt" : "Effectuer le retrait" %>
                    </button>
                    <a href="${pageContext.request.contextPath}/transactions" class="btn btn-secondary">Annuler</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>