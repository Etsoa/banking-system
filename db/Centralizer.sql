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

-- ---------------------------
-- Jeu de données de test
-- ---------------------------

-- Roles
INSERT INTO roles (nom) VALUES
('ADMIN'),
('MANAGER'),
('USER');

-- Utilisateurs
INSERT INTO utilisateurs (nom_utilisateur, mot_de_passe, telephone, email, statut, date_creation)
VALUES
('admin', 'adminpass', '+261330000001', 'admin@example.com', 'ACTIVE', now()),
('manager', 'managerpass', '+261330000002', 'manager@example.com', 'ACTIVE', now()),
('user1', 'userpass', '+261330000003', 'user1@example.com', 'ACTIVE', now());

-- Attributions de role (historique)
INSERT INTO historiques_role_utilisateur (date_assignation, id_role, id_utilisateur)
VALUES
 (now()::date, 1, 1),
 (now()::date, 2, 2),
 (now()::date, 3, 3);

-- Clients
INSERT INTO clients (nom, prenom, date_naissance, adresse, telephone, email)
VALUES
('Rakoto', 'Andry', '1985-03-12', 'Rue A, Antananarivo', '+261330111111', 'andry.rakoto@example.com'),
('Rabe', 'Mina', '1990-07-05', 'Rue B, Antananarivo', '+261330222222', 'mina.rabe@example.com'),
('Rajaonarivelo', 'Hery', '1978-11-22', 'Rue C, Antananarivo', '+261330333333', 'hery.rajao@example.com'),
('Razafimahefa', 'Lalao', '1995-01-17', 'Rue D, Antananarivo', '+261330444444', 'lalao.raz@example.com'),
('Andriamanana', 'Jean', '1982-05-09', 'Rue E, Antananarivo', '+261330555555', 'jean.andri@example.com');

-- Historiques de revenus (valeur en texte pour compatibilité existante)
INSERT INTO historiques_revenus (date_changement, valeur, id_client)
VALUES
('2024-01-01', '2500', 1),
('2024-06-01', '3000', 1),
('2024-03-01', '1200', 2),
('2024-04-15', '800', 3),
('2024-02-10', '1500', 4),
('2024-08-20', '2200', 5);

-- Configurations (exemples généraux)
INSERT INTO configurations (nom, min_, max_, valeur, actif)
VALUES
('TAUX_INTERET_BASE', 0, 100, 5.00, TRUE),
('FRAIS_RETARD_PAR_JOUR', 0, 100, 0.10, TRUE),
('PLAFOND_PRET_PAR_REVENU', 0, 10000000, 0, TRUE);

-- Historiques statut client
INSERT INTO historiques_statut_client (date_changement, id_client, statut)
VALUES
 (now(), 1, 'ACTIF'),
 (now(), 2, 'ACTIF'),
 (now(), 3, 'INACTIF'),
 (now(), 4, 'ACTIF'),
 (now(), 5, 'ACTIF');

-- Petit message pour indiquer que le jeu de données a été inséré
-- SELECT 'Test data inserted' as info;
