<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.centralizer.dto.CompteCourant" %>
<%@ page import="com.example.centralizer.dto.Transaction" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Détails Compte - Banking System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="container">
            <h1>Banking System Centralizer</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/home">Accueil</a>
                <a href="${pageContext.request.contextPath}/comptes">Comptes</a>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary">Déconnexion</a>
            </div>
        </div>
    </nav>
    
    <div class="container">
        <%
            CompteCourant compte = (CompteCourant) request.getAttribute("compte");
            List<Transaction> transactions = (List<Transaction>) request.getAttribute("transactions");
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.FRANCE);
            
            if (compte != null) {
        %>
        <h2>Détails du Compte #<%= compte.getIdCompte() %></h2>
        
        <div class="account-info">
            <h3>Informations du compte</h3>
            <p><strong>ID Compte:</strong> <%= compte.getIdCompte() %></p>
            <p><strong>Solde:</strong> <span class="balance"><%= currencyFormatter.format(compte.getSolde()) %></span></p>
        </div>
        
        <div class="actions">
            <h3>Opérations</h3>
            <button onclick="document.getElementById('depotModal').style.display='block'" class="btn btn-success">
                Dépôt
            </button>
            <button onclick="document.getElementById('retraitModal').style.display='block'" class="btn btn-warning">
                Retrait
            </button>
        </div>
        
        <!-- Historique des transactions -->
        <div class="transactions-history">
            <h3>Historique des transactions</h3>
            
            <%
                if (transactions != null && !transactions.isEmpty()) {
            %>
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Date</th>
                            <th>Type</th>
                            <th>Montant</th>
                            <th>Contrepartie</th>
                            <th>Statut</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            for (Transaction t : transactions) {
                                String typeClass = t.getTypeTransaction() != null ? 
                                    "type-" + t.getTypeTransaction().name() : "";
                                String statusClass = t.getStatutTransaction() != null ?
                                    "status-" + t.getStatutTransaction().name() : "";
                        %>
                        <tr>
                            <td>#<%= t.getIdTransaction() %></td>
                            <td><%= t.getDateTransaction() %></td>
                            <td class="<%= typeClass %>">
                                <%= t.getTypeTransaction() != null ? 
                                    t.getTypeTransaction().name().toUpperCase() : "N/A" %>
                            </td>
                            <td><%= currencyFormatter.format(t.getMontant()) %></td>
                            <td>
                                <% if (t.getIdCompteContrepartie() != null) { %>
                                    Compte #<%= t.getIdCompteContrepartie() %>
                                <% } else { %>
                                    -
                                <% } %>
                            </td>
                            <td>
                                <span class="status-badge <%= statusClass %>">
                                    <%= t.getStatutTransaction() != null ? 
                                        t.getStatutTransaction().name() : "N/A" %>
                                </span>
                            </td>
                        </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
            </div>
            <%
                } else {
            %>
            <p>Aucune transaction pour ce compte.</p>
            <%
                }
            %>
        </div>
        
        <!-- Modal Dépôt -->
        <div id="depotModal" class="modal">
            <div class="modal-content">
                <span class="close" onclick="document.getElementById('depotModal').style.display='none'">&times;</span>
                <h3>Effectuer un dépôt</h3>
                <form method="post" action="${pageContext.request.contextPath}/comptes">
                    <input type="hidden" name="action" value="depot">
                    <input type="hidden" name="idCompte" value="<%= compte.getIdCompte() %>">
                    <div class="form-group">
                        <label for="montantDepot">Montant:</label>
                        <input type="number" id="montantDepot" name="montant" step="0.01" min="0.01" required>
                    </div>
                    <button type="submit" class="btn btn-success">Déposer</button>
                </form>
            </div>
        </div>
        
        <!-- Modal Retrait -->
        <div id="retraitModal" class="modal">
            <div class="modal-content">
                <span class="close" onclick="document.getElementById('retraitModal').style.display='none'">&times;</span>
                <h3>Effectuer un retrait</h3>
                <form method="post" action="${pageContext.request.contextPath}/comptes">
                    <input type="hidden" name="action" value="retrait">
                    <input type="hidden" name="idCompte" value="<%= compte.getIdCompte() %>">
                    <div class="form-group">
                        <label for="montantRetrait">Montant:</label>
                        <input type="number" id="montantRetrait" name="montant" step="0.01" min="0.01" required>
                    </div>
                    <button type="submit" class="btn btn-warning">Retirer</button>
                </form>
            </div>
        </div>
        <%
            } else {
        %>
        <div class="alert alert-error">
            Compte non trouvé
        </div>
        <%
            }
        %>
    </div>
</body>
</html>
