@echo off
echo ====================================
echo Configuration de la DataSource CentralizerDS dans WildFly
echo ====================================

set WILDFLY_HOME=C:\apk\wildfly-37.0.1.Final
set JBOSS_CLI=%WILDFLY_HOME%\bin\jboss-cli.bat

echo.
echo Connexion au serveur WildFly...
echo.

REM Commandes JBoss CLI pour créer la DataSource
%JBOSS_CLI% --connect --commands="data-source add --name=CentralizerDS --jndi-name=java:jboss/datasources/CentralizerDS --driver-name=postgresql --connection-url=jdbc:postgresql://localhost:5432/centralizer --user-name=postgres --password=EtsoaMahimba --enabled=true"

if %errorlevel% equ 0 (
    echo.
    echo ====================================
    echo DataSource CentralizerDS creee avec succes !
    echo ====================================
    echo.
    echo JNDI: java:jboss/datasources/CentralizerDS
    echo Base: centralizer
    echo.
) else (
    echo.
    echo ERREUR: Impossible de creer la DataSource
    echo Verifiez que WildFly est demarre
    echo.
)

pause
