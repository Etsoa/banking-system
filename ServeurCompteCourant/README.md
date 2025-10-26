# CompteCourant EJB

Module EJB pur pour la gestion des comptes courants.

## Structure
- EJB Stateless exposant une interface locale et distante
- Modèles `CompteCourant` et `Transaction` avec persistance JPA
- Exception personnalisée `CompteCourantException`
- Fichier de configuration EJB `ejb-jar.xml`
- Configuration JPA `persistence.xml`

## Build
```sh
mvn clean package
```

## Déploiement Docker
```sh
docker-compose build
docker-compose up -d
```

Admin WildFly : fetraniaina / EtsoaMahimba
Port application : 8082
Port admin : 9991

## Integration avec Centralizer
```java
Context ctx = new InitialContext();
CompteCourantServiceRemote remote = (CompteCourantServiceRemote) 
    ctx.lookup("ejb:/comptecourant/CompteCourantServiceBean!com.example.comptecourant.ejb.CompteCourantServiceRemote");
```
