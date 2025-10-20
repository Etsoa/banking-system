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
   type_transaction VARCHAR(20) NOT NULL CHECK (type_transaction IN ('debit', 'credit', 'virement')),
   statut_transaction VARCHAR(20) NOT NULL CHECK (statut_transaction IN ('en_attente', 'confirmee', 'refusee')),
   FOREIGN KEY (id_compte) REFERENCES comptes(id_compte)
       ON DELETE CASCADE
       ON UPDATE CASCADE,
   FOREIGN KEY (id_compte_contrpartie) REFERENCES comptes(id_compte)
       ON DELETE SET NULL
       ON UPDATE CASCADE
);
