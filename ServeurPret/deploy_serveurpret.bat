@echo off
set PROJECT_DIR=%~dp0
set WILDFLY_DEPLOY_DIR=C:\apk\wildfly-37.0.1.Final\standalone\deployments
set WAR_NAME=pret.war

cd /d "%PROJECT_DIR%"

call mvn clean package
if errorlevel 1 exit /b 1

echo Suppression de l'ancien WAR...
if exist "%WILDFLY_DEPLOY_DIR%\%WAR_NAME%" del /F /Q "%WILDFLY_DEPLOY_DIR%\%WAR_NAME%"

echo Copie du nouveau WAR...
xcopy "%PROJECT_DIR%\target\%WAR_NAME%" "%WILDFLY_DEPLOY_DIR%\" /Y /R /F /C

echo Deploiement termine avec succes !

pause
