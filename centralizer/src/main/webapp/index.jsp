<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Accueil - Banking System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="container">
            <h1>Banking System Centralizer</h1>
            <div class="nav-links">
                <span>Bienvenue, <%= request.getAttribute("username") %></span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary">Déconnexion</a>
            </div>
        </div>
    </nav>
    
    <div class="container">
        <div class="dashboard">
            <h2>Tableau de bord</h2>
            
            <div class="dashboard-cards">
                <div class="card">
                    <h3>Comptes Courants</h3>
                    <p>Gérer les comptes courants</p>
                    <a href="${pageContext.request.contextPath}/comptes" class="btn btn-primary">Accéder</a>
                </div>
                
                <div class="card">
                    <h3>Transactions</h3>
                    <p>Consulter et valider les transactions</p>
                    <a href="${pageContext.request.contextPath}/transactions" class="btn btn-primary">Accéder</a>
                </div>
                
                <div class="card">
                    <h3>Transactions en attente</h3>
                    <p>Valider ou refuser les transactions</p>
                    <a href="${pageContext.request.contextPath}/transactions/en-attente" class="btn btn-warning">Accéder</a>
                </div>
<%--                 
                <div class="card">
                    <h3>Comptes Dépôt</h3>
                    <p>Gérer les comptes dépôt</p>
                    <a href="${pageContext.request.contextPath}/comptes-depot" class="btn btn-primary">Accéder</a>
                </div>
                
                <div class="card">
                    <h3>Prêts</h3>
                    <p>Gérer les prêts</p>
                    <a href="${pageContext.request.contextPath}/prets" class="btn btn-primary">Accéder</a>
                </div>
                
                <div class="card">
                    <h3>Transactions</h3>
                    <p>Historique des transactions</p>
                    <a href="${pageContext.request.contextPath}/transactions" class="btn btn-primary">Accéder</a>
                </div> --%>
            </div>
        </div>
    </div>
</body>
</html>
