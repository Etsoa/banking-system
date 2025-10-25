# Serveur Echange - Docker

## 🐳 Construction de l'image Docker

```powershell
# Se placer dans le dossier echange
cd echange

# Construire l'image
docker build -t echange-service:latest .
```

## 🚀 Démarrage avec Docker

### Option 1 : Docker simple
```powershell
# Lancer le conteneur
docker run -d `
  --name echange-service `
  -p 8080:8080 `
  echange-service:latest

# Vérifier les logs
docker logs -f echange-service

# Arrêter le conteneur
docker stop echange-service

# Supprimer le conteneur
docker rm echange-service
```

### Option 2 : Docker Compose (recommandé)
```powershell
# Démarrer le service
docker-compose up -d

# Voir les logs
docker-compose logs -f

# Arrêter le service
docker-compose down
```

## 🧪 Tester le service

Une fois le conteneur démarré, tester avec :

```powershell
# Récupérer tous les taux de change
curl http://localhost:8080/echange/api/echanges

# Récupérer les taux actifs
curl http://localhost:8080/echange/api/echanges/actifs

# Convertir 100 EUR en MGA à une date donnée
curl "http://localhost:8080/echange/api/echanges/convertir/vers-ariary?devise=EUR/MGA&montant=100&date=2024-03-15"
```

## 📊 Vérifier le déploiement

```powershell
# Vérifier que le conteneur tourne
docker ps

# Vérifier le health check
docker inspect echange-service | Select-String -Pattern "Health"

# Accéder aux logs WildFly
docker exec -it echange-service tail -f /opt/jboss/wildfly/standalone/log/server.log
```

## 🔧 Configuration

### Ports exposés
- **8080** : HTTP (API REST)
- **9990** : Console d'administration WildFly (optionnel)

### Variables d'environnement
- `JAVA_OPTS` : Options JVM (défaut: `-Xms512m -Xmx1024m`)

## 🌐 Intégration avec le centralizer

Le centralizer doit pointer vers :
```
http://echange-service:8080/echange/api
```

Si le centralizer est aussi dans Docker, utiliser le nom du service.  
Sinon, utiliser `http://localhost:8080/echange/api`.

## 🛠️ Débogage

```powershell
# Entrer dans le conteneur
docker exec -it echange-service bash

# Vérifier le WAR déployé
docker exec echange-service ls -la /opt/jboss/wildfly/standalone/deployments/

# Redémarrer le service
docker-compose restart
```

## 🗑️ Nettoyage

```powershell
# Arrêter et supprimer tout
docker-compose down

# Supprimer l'image
docker rmi echange-service:latest

# Nettoyer les volumes (si nécessaire)
docker volume prune
```
