# Architecture de Session HTTP - Centralizer

## 📋 Vue d'ensemble

L'authentification et la gestion de session utilisateur sont maintenant entièrement basées sur la **session HTTP (servlet session)** plutôt que sur les EJB Stateful. Cela offre une meilleure persistance et une gestion cohérente à travers les requêtes HTTP.

## 🔐 Flux d'Authentification

```
1. Utilisateur accède à /login
   ↓
2. LoginServlet.doPost() reçoit username/password
   ↓
3. Appel CompteCourantServiceImpl.login(username, password)
   - Authentifie auprès du serveur backend via EJB JNDI
   - Retourne LoginResponse avec succès/erreur
   ↓
4. Si succès, récupère SessionUtilisateur via CompteCourantServiceImpl.getSessionUtilisateur()
   ↓
5. Stocke SessionUtilisateur dans HttpSession
   - Clé: "sessionUtilisateur"
   - Persiste à travers toutes les requêtes HTTP du client
   ↓
6. Redirection vers /home avec session active
```

## 📦 Composants Clés

### 1. **SessionUtilisateur DTO**
Stocke les données de session utilisateur:
```java
- idUtilisateur (Integer)
- nomUtilisateur (String)
- roleUtilisateur (Integer)  // ID du rôle
- actionsRoles (List<ActionRoleDTO>)  // Permissions
```

**Localisation:** `com.example.centralizer.dto.comptecourant.SessionUtilisateur`

### 2. **LoginServlet**
Gère l'authentification et la création de session HTTP:
- **Endpoint GET `/login`**: Affiche la page JSP de login
- **Endpoint POST `/login`**: Traite l'authentification et crée la session HTTP
- **Endpoint `/logout`**: Invalide la session et déconnecte l'utilisateur
- **Stockage:** SessionUtilisateur dans `session.setAttribute("sessionUtilisateur", ...)`

**Localisation:** `com.example.centralizer.servlets.LoginServlet`

### 3. **SessionManager (Utility)**
Classe helper pour accéder facilement à la session:
```java
// Récupérer la SessionUtilisateur
SessionUtilisateur session = SessionManager.getSessionUtilisateur(request);

// Vérifier l'authentification
boolean isAuth = SessionManager.isAuthenticated(request);

// Récupérer les infos utilisateur
Integer userId = SessionManager.getCurrentUserId(request);
String username = SessionManager.getCurrentUsername(request);
Integer roleId = SessionManager.getCurrentUserRoleId(request);

// Vérifier les permissions
boolean hasAccess = SessionManager.hasPermission(request, "TABLE", "ACTION");
```

**Localisation:** `com.example.centralizer.servlets.SessionManager`

### 4. **AuthenticationFilter**
Filtre pour protéger les ressources authentifiées:
- Intercepte les requêtes vers `/home`, `/comptes/*`, `/transactions/*`, `/echanges/*`
- Redirige vers `/login` si l'utilisateur n'est pas authentifié
- Permet la continuation si l'utilisateur est authentifié

**Localisation:** `com.example.centralizer.servlets.AuthenticationFilter`

## 🔄 Services Refactorisés

### AuthenticationServiceImpl (EJB Stateful)
- Maintient une `SessionUtilisateur` en état interne
- Offre les méthodes:
  - `login(username, password)` → `LoginResponse`
  - `logout()` → void
  - `getSessionUtilisateur()` → `SessionUtilisateur`
  - `getCurrentUserId()` → `Integer`
  - `getCurrentUsername()` → `String`
  - `getCurrentUserRoleId()` → `Integer`

### CompteCourantServiceImpl (EJB Stateful)
- Délègue l'authentification à `AuthenticationServiceImpl`
- Expose la session utilisateur via:
  - `getSessionUtilisateur()` → `SessionUtilisateur`
  - `getCurrentUserRoleId()` → `Integer`

## 💾 Stockage et Cycle de Vie

```
Requête 1 (POST /login)
├─ LoginServlet crée SessionUtilisateur
├─ Stocke dans HttpSession
└─ SessionUtilisateur persiste jusqu'à session.invalidate()

Requête 2 (GET /home)
├─ AuthenticationFilter vérifie SessionUtilisateur dans session
├─ HomeServlet accède à SessionUtilisateur via SessionManager
└─ Utilise les données pour afficher le contenu personnalisé

Requête 3 (POST /logout)
├─ LoginServlet récupère SessionUtilisateur de la session
├─ Invalide la session HTTP
└─ Redirige vers /login
```

## 🚀 Utilisation dans les Servlets

### Exemple dans HomeServlet:
```java
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    // Vérifier l'authentification
    SessionUtilisateur session = SessionManager.getSessionUtilisateur(req);
    if (session == null) {
        resp.sendRedirect(req.getContextPath() + "/login");
        return;
    }
    
    // Utiliser les infos utilisateur
    String username = session.getNomUtilisateur();
    Integer roleId = session.getRoleUtilisateur();
    
    // Vérifier les permissions
    if (session.aAutorisationPour("COMPTES", "READ")) {
        // Afficher les comptes
    }
}
```

## ✅ Avantages de cette Architecture

1. **Persistance HTTP-native**: La session persiste automatiquement entre les requêtes HTTP
2. **Scalabilité**: Les sessions sont gérées par le container servlet (clustering supporté)
3. **Sécurité**: Les cookies de session sont gérés par le container
4. **Séparation des préoccupations**: 
   - EJB gère la logique métier et l'authentification distante
   - Session HTTP gère la persistance pour l'utilisateur
5. **Accès facile**: `SessionManager` offre une API simple pour tous les servlets
6. **Permissions intégrées**: `aAutorisationPour()` disponible directement sur `SessionUtilisateur`

## 🔗 Points d'Intégration

- **Autres servlets**: Utiliser `SessionManager` pour accéder à la session
- **JSP**: `${sessionUtilisateur.nomUtilisateur}` via session HTTP
- **REST/API futures**: Retourner l'info depuis `SessionManager.getSessionUtilisateur(request)`
- **Permissions**: Vérifier via `session.aAutorisationPour(table, action)` ou `SessionManager.hasPermission()`

## 📝 Prochaines Étapes

1. ✅ Session HTTP avec SessionUtilisateur (COMPLÉTÉ)
2. ⏳ Implémenter `getSessionUtilisateur()` dans EJB pour retourner la SessionUtilisateur peuplée
3. ⏳ Peupler `actionsRoles` lors du login (récupérer depuis le serveur basé sur le rôle)
4. ⏳ Mettre à jour les autres servlets pour utiliser `SessionManager`
5. ⏳ Tester avec des utilisateurs réels et vérifier les permissions
