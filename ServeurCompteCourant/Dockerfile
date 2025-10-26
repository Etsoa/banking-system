# Use WildFly base image
FROM quay.io/wildfly/wildfly:37.0.0.Final-jdk17

COPY target/comptecourant.jar /opt/jboss/wildfly/standalone/deployments/

USER root
# Création de l'utilisateur admin WildFly
RUN /opt/jboss/wildfly/bin/add-user.sh -u fetraniaina -p EtsoaMahimba -g admin
USER jboss

EXPOSE 8080 9990
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
