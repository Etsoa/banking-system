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
INSERT INTO actions_roles (nom_table, nom_action, role_minimum) VALUES 
('comptes', 'create', 2),
('comptes', 'read', 3),
('comptes', 'update', 2),
('comptes', 'delete', 1),
('transactions', 'create', 3),
('transactions', 'read', 3),
('transactions', 'valider', 2),
('transactions', 'refuser', 2);

-- Insertion des utilisateurs
-- Mot de passe pour tous : 'password123' (à hasher en production)
INSERT INTO utilisateurs (nom_utilisateur, mot_de_passe, id_direction, role_utilisateur) VALUES 
('admin', 'password123', 1, 1),
('manager', 'password123', 2, 2),
('employe1', 'password123', 3, 3),
('employe2', 'password123', 4, 3),
('caissier', 'password123', 5, 4);

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

-- Insertion des transactions
INSERT INTO transactions (montant, id_compte, id_compte_contrpartie, type_transaction, statut_transaction, date_transaction) VALUES 
-- Transactions confirmées
(200.00, 1, NULL, 'depot', 'confirmee', '2025-10-15'),
(150.50, 1, NULL, 'retrait', 'confirmee', '2025-10-16'),
(500.00, 2, NULL, 'depot', 'confirmee', '2025-10-17'),
(100.25, 3, NULL, 'depot', 'confirmee', '2025-10-18'),
(2000.00, 4, NULL, 'depot', 'confirmee', '2025-10-19'),

-- Transactions en attente
(300.00, 2, NULL, 'retrait', 'en_attente', '2025-10-20'),
(75.50, 3, NULL, 'retrait', 'en_attente', '2025-10-20'),
(1000.00, 4, NULL, 'retrait', 'en_attente', '2025-10-20'),

-- Transactions refusées
(5000.00, 3, NULL, 'retrait', 'refusee', '2025-10-19'),
(15000.00, 4, NULL, 'retrait', 'refusee', '2025-10-18'),

-- Transferts entre comptes
(250.00, 1, 2, 'retrait', 'confirmee', '2025-10-17'),
(100.00, 2, 3, 'retrait', 'confirmee', '2025-10-18'),
(50.75, 5, 6, 'retrait', 'en_attente', '2025-10-20');
