CREATE DATABASE banking_system_centralizer ;
\c banking_system_centralizer;  
CREATE TABLE clients(
   id_client SERIAL,
   nom VARCHAR(50)  NOT NULL,
   prenom VARCHAR(50)  NOT NULL,
   date_naissance DATE NOT NULL,
   adresse TEXT,
   telephone VARCHAR(20) ,
   email VARCHAR(100)  NOT NULL,
   PRIMARY KEY(id_client)
);

CREATE TABLE historiques_taux(
   id_historique_taux SERIAL,
   date_debut TIMESTAMP NOT NULL,
   nom VARCHAR(50)  NOT NULL,
   valeur NUMERIC(5,2)   NOT NULL,
   actif BOOLEAN NOT NULL,
   PRIMARY KEY(id_historique_taux)
);

CREATE TABLE utilisateurs(
   id_utilisateur SERIAL,
   nom_utilisateur VARCHAR(50)  NOT NULL,
   mot_de_passe TEXT NOT NULL,
   telephone VARCHAR(50) ,
   email VARCHAR(100)  NOT NULL,
   statut VARCHAR(50)  NOT NULL,
   date_creation TIMESTAMP NOT NULL,
   PRIMARY KEY(id_utilisateur),
   UNIQUE(nom_utilisateur)
);

CREATE TABLE roles(
   id_role SERIAL,
   nom VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id_role)
);

CREATE TABLE historiques_role_utilisateur(
   id_historique_role_utilisateur SERIAL,
   date_assignation DATE NOT NULL,
   id_role INTEGER NOT NULL,
   id_utilisateur INTEGER NOT NULL,
   PRIMARY KEY(id_historique_role_utilisateur),
   FOREIGN KEY(id_role) REFERENCES roles(id_role) on delete cascade on update cascade,
   FOREIGN KEY(id_utilisateur) REFERENCES utilisateurs(id_utilisateur) on delete cascade on update cascade
);

CREATE TABLE historiques_revenus(
   id_historique_revenus SERIAL,
   date_changement DATE NOT NULL,
   valeur VARCHAR(50)  NOT NULL,
   id_client INTEGER NOT NULL,
   PRIMARY KEY(id_historique_revenus),
   FOREIGN KEY(id_client) REFERENCES clients(id_client) on delete cascade on update cascade
);

CREATE TABLE configurations(
   id_configuration SERIAL,
   nom VARCHAR(50)  NOT NULL,
   min_ NUMERIC(15,2)   NOT NULL,
   max_ NUMERIC(15,2)   NOT NULL,
   valeur NUMERIC(15,2)   NOT NULL,
   actif BOOLEAN NOT NULL,
   PRIMARY KEY(id_configuration)
);

CREATE TABLE historiques_statut_client(
   id_historique_statut_client SERIAL,
   date_changement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   id_client INTEGER NOT NULL,
   statut VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id_historique_statut_client),
   FOREIGN KEY(id_client) REFERENCES clients(id_client) on delete cascade on update cascade
);
