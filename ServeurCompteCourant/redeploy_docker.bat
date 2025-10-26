@echo off
echo ========================================
echo Redeploiement du serveur CompteCourant EJB
echo ========================================
echo.

echo [1/3] Compilation du projet...
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: La compilation a echoue!
    pause
    exit /b 1
)
echo.

echo [2/3] Verification du conteneur Docker...
docker ps | findstr comptecourant >nul
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: Le conteneur comptecourant n'est pas en cours d'execution!
    echo Veuillez d'abord demarrer le conteneur avec: docker-compose up -d
    pause
    exit /b 1
)
echo.

echo [3/3] Copie du JAR dans le conteneur...
if not exist target\comptecourant.jar (
    echo ERREUR: Le fichier target\comptecourant.jar est introuvable!
    echo Verifiez le nom du JAR dans target\ apres compilation.
    pause
    exit /b 1
)
docker cp target\comptecourant.jar comptecourant-ejb:/opt/jboss/wildfly/standalone/deployments/
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: La copie du JAR a echoue!
    pause
    exit /b 1
)
echo.

echo ========================================
echo Redeploiement termine avec succes!
echo ========================================
echo.
echo Le JAR a ete copie. WildFly va le redeployer automatiquement.
echo Patientez quelques secondes...
echo.
echo Pour voir les logs: docker-compose logs -f
echo Pour acceder au serveur: http://localhost:8082
echo.
pause
