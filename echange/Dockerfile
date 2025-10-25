FROM maven:3.9.9-eclipse-temurin-17 AS builder

# Copier les fichiers du projet
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Builder le WAR
RUN mvn clean package -DskipTests

# Image finale avec WildFly
FROM quay.io/wildfly/wildfly:37.0.1.Final-jdk17

# Copier le WAR depuis l'étape de build
COPY --from=builder /app/target/echange.war /opt/jboss/wildfly/standalone/deployments/

# Exposer le port HTTP
EXPOSE 8080

# Commande pour démarrer WildFly
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]
