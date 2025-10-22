-- Création de la base
CREATE DATABASE banking_system_compte_courant;
\c banking_system_compte_courant;

-- Table comptes
CREATE TABLE comptes(
   id_compte SERIAL PRIMARY KEY,
   solde NUMERIC(12,2) NOT NULL DEFAULT 0
);

-- Table directions
CREATE TABLE directions(
   id_direction SERIAL PRIMARY KEY,
   libelle VARCHAR(50),
   niveau INT
);

-- Table utilisateurs
CREATE TABLE utilisateurs(
   id_utilisateur SERIAL PRIMARY KEY,
   nom_utilisateur VARCHAR(50) NOT NULL UNIQUE,
   mot_de_passe VARCHAR(100) NOT NULL,
   id_direction INT,
   role_utilisateur INT,
   FOREIGN KEY (id_direction) REFERENCES directions(id_direction)
       ON DELETE CASCADE
       ON UPDATE CASCADE
);

-- Table actions_roles
CREATE TABLE actions_roles(
   id_action_role SERIAL PRIMARY KEY,
   nom_table VARCHAR(20),
   nom_action VARCHAR(20),
   role_minimum INT
);

-- Table transactions
CREATE TABLE transactions(
   id_transaction SERIAL PRIMARY KEY,
   date_transaction DATE NOT NULL DEFAULT CURRENT_DATE,
   montant NUMERIC(12,2) NOT NULL,
   id_compte INT NOT NULL,
   id_compte_contrpartie INT,
   type_transaction VARCHAR(20) NOT NULL CHECK (type_transaction IN ('retrait', 'depot')),
   statut_transaction VARCHAR(20) NOT NULL CHECK (statut_transaction IN ('en_attente', 'confirmee', 'refusee')),
   FOREIGN KEY (id_compte) REFERENCES comptes(id_compte)
       ON DELETE CASCADE
       ON UPDATE CASCADE,
   FOREIGN KEY (id_compte_contrpartie) REFERENCES comptes(id_compte)
       ON DELETE SET NULL
       ON UPDATE CASCADE
);

-- ===================================================
-- DONNÉES DE TEST
-- ===================================================

-- Insertion des directions
INSERT INTO directions (libelle, niveau) VALUES 
('Direction Générale', 1),
('Direction des Opérations', 2),
('Service Clientèle', 3),
('Agence Centre', 4),
('Agence Nord', 4);

-- Insertion des actions et rôles
-- Logique : roleUtilisateur >= role_minimum
-- Plus le numéro est élevé, plus le rôle a de pouvoir (1=Caissier, 2=Employé, 3=Manager, 4=Admin)
INSERT INTO actions_roles (nom_table, nom_action, role_minimum) VALUES 
('comptes', 'create', 3),        
('comptes', 'read', 2),           
('comptes', 'update', 3),         
('comptes', 'delete', 4),         
('transactions', 'create', 4),    
('transactions', 'read', 2),      
('transactions', 'update', 3),
('transactions', 'delete', 4);   


-- Insertion des utilisateurs
-- Mot de passe pour tous : 'password123' (à hasher en production)
-- Rôles : 1=Caissier, 2=Employé, 3=Manager, 4=Admin
INSERT INTO utilisateurs (nom_utilisateur, mot_de_passe, id_direction, role_utilisateur) VALUES 
('admin', 'password123', 1, 4),       -- Admin = rôle 4 (accès total)
('manager', 'password123', 2, 3),     -- Manager = rôle 3
('employe1', 'password123', 3, 2),    -- Employé = rôle 2
('employe2', 'password123', 4, 2),    -- Employé = rôle 2
('caissier', 'password123', 5, 1);    -- Caissier = rôle 1 (accès minimal)

-- Insertion des comptes
INSERT INTO comptes (solde) VALUES 
(1500.50),
(2750.00),
(500.25),
(10000.00),
(250.75),
(5000.00),
(0.00),
(1200.30);
