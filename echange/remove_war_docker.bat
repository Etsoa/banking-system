@echo off
echo ========================================
echo Suppression du WAR dans Docker
echo ========================================
echo.

echo Verification du conteneur Docker...
docker ps | findstr echange-service >nul
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: Le conteneur echange-service n'est pas en cours d'execution!
    pause
    exit /b 1
)
echo.

echo Suppression du WAR et des fichiers de deploiement...
docker exec echange-service rm -f /opt/jboss/wildfly/standalone/deployments/echange.war
docker exec echange-service rm -f /opt/jboss/wildfly/standalone/deployments/echange.war.deployed
docker exec echange-service rm -f /opt/jboss/wildfly/standalone/deployments/echange.war.failed
docker exec echange-service rm -f /opt/jboss/wildfly/standalone/deployments/echange.war.undeployed
echo.

echo ========================================
echo WAR supprime avec succes!
echo ========================================
echo.
echo L'application a ete supprimee du serveur WildFly.
echo.
pause
