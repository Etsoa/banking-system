#!/bin/bash

echo "========================================"
echo "Redeploiement du serveur Echange Docker"
echo "========================================"
echo ""

echo "[1/3] Compilation du projet..."
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "ERREUR: La compilation a echoue!"
    exit 1
fi
echo ""

echo "[2/3] Verification du conteneur Docker..."
if ! docker ps | grep -q echange-service; then
    echo "ERREUR: Le conteneur echange-service n'est pas en cours d'execution!"
    echo "Veuillez d'abord demarrer le conteneur avec: docker-compose up -d"
    exit 1
fi
echo ""

echo "[3/3] Copie du WAR dans le conteneur..."
docker cp target/echange.war echange-service:/opt/jboss/wildfly/standalone/deployments/
if [ $? -ne 0 ]; then
    echo "ERREUR: La copie du WAR a echoue!"
    exit 1
fi
echo ""

echo "========================================"
echo "Redeploiement termine avec succes!"
echo "========================================"
echo ""
echo "Le WAR a ete copie. WildFly va le redeployer automatiquement."
echo "Patientez quelques secondes..."
echo ""
echo "Pour voir les logs: docker-compose logs -f"
echo "Pour tester l'API: curl http://localhost:8081/echange/api/echanges"
echo ""
