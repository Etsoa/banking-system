@echo off
echo ========================================
echo Ajout d'utilisateur WildFly dans Docker
echo ========================================
echo.

echo Verification du conteneur Docker...
docker ps | findstr echange-service >nul
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: Le conteneur echange-service n'est pas en cours d'execution!
    echo Veuillez d'abord demarrer le conteneur avec: docker-compose up -d
    pause
    exit /b 1
)
echo.

echo Ajout de l'utilisateur admin...
echo Username: fetraniaina
echo Password: EtsoaMahimba
echo.

docker exec -it echange-service /opt/jboss/wildfly/bin/add-user.sh fetraniaina EtsoaMahimba --silent
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: L'ajout de l'utilisateur a echoue!
    pause
    exit /b 1
)
echo.

echo ========================================
echo Utilisateur ajoute avec succes!
echo ========================================
echo.
echo Username: fetraniaina
echo Password: EtsoaMahimba
echo.
echo Console d'administration accessible sur:
echo http://localhost:9991/console
echo.
echo Note: Si vous ne pouvez pas vous connecter, redemarrez le conteneur:
echo   docker-compose restart
echo.
pause
