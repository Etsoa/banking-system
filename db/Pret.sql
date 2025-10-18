-- Creation de la base
CREATE DATABASE banking_system_pret;
\c banking_system_pret;

-- Table des types de remboursement
CREATE TABLE types_remboursement(
   id_type_remboursement SERIAL PRIMARY KEY,
   nom VARCHAR(50) NOT NULL,
   actif BOOLEAN NOT NULL DEFAULT TRUE
);

-- Table des modalites de remboursement
CREATE TABLE modalites(
   id_modalite SERIAL PRIMARY KEY,
   libelle VARCHAR(50) NOT NULL,
   nombre_mois INTEGER NOT NULL,
   actif BOOLEAN NOT NULL DEFAULT TRUE
);

-- Table des statuts de pret
CREATE TABLE statuts_pret(
   id_statut_pret SERIAL PRIMARY KEY,
   libelle VARCHAR(50) NOT NULL,
   actif BOOLEAN NOT NULL DEFAULT TRUE
);

-- Table des methodes de remboursement
CREATE TABLE methodes_remboursement(
   id_methode_remboursement SERIAL PRIMARY KEY,
   libelle VARCHAR(50) NOT NULL,
   actif BOOLEAN NOT NULL DEFAULT TRUE
);

-- Table des prets
CREATE TABLE prets(
   id_pret SERIAL PRIMARY KEY,
   montant NUMERIC(12,2) NOT NULL,
   duree_periode INTEGER NOT NULL,
   date_debut DATE NOT NULL,
   client_id VARCHAR(50) NOT NULL,
   id_statut_pret INTEGER NOT NULL,
   id_modalite INTEGER NOT NULL,
   id_type_remboursement INTEGER NOT NULL,
   FOREIGN KEY(id_statut_pret) REFERENCES statuts_pret(id_statut_pret) ON DELETE CASCADE ON UPDATE CASCADE,
   FOREIGN KEY(id_modalite) REFERENCES modalites(id_modalite) ON DELETE CASCADE ON UPDATE CASCADE,
   FOREIGN KEY(id_type_remboursement) REFERENCES types_remboursement(id_type_remboursement) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Table des statuts de remboursement
CREATE TABLE statuts_remboursement(
    id_statut_remboursement SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

-- Table des remboursements
CREATE TABLE remboursements(
   id_remboursement SERIAL PRIMARY KEY,
   date_paiement DATE,
   montant NUMERIC(12,2) NOT NULL,
   jours_retard INTEGER DEFAULT 0,
   id_statut_remboursement INTEGER,
   id_methode_remboursement INTEGER NOT NULL,
   id_pret INTEGER NOT NULL,
   FOREIGN KEY(id_statut_remboursement) REFERENCES statuts_remboursement(id_statut_remboursement),
   FOREIGN KEY(id_methode_remboursement) REFERENCES methodes_remboursement(id_methode_remboursement) ON DELETE CASCADE ON UPDATE CASCADE,
   FOREIGN KEY(id_pret) REFERENCES prets(id_pret) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Table du tableau d'amortissement (annuites)
CREATE TABLE amortissement_pret(
    id_amortissement SERIAL PRIMARY KEY,
    id_pret INTEGER NOT NULL,
    periode INTEGER NOT NULL,
    date_echeance DATE NOT NULL,
    capital_debut NUMERIC(12,2) NOT NULL,
    interets NUMERIC(12,2) NOT NULL,
    amortissement NUMERIC(12,2) NOT NULL,
    annuite NUMERIC(12,2) NOT NULL,
    capital_restant NUMERIC(12,2) NOT NULL,
    FOREIGN KEY(id_pret) REFERENCES prets(id_pret) ON DELETE CASCADE ON UPDATE CASCADE
);

-- INDEX pour accelerer les recherches par id_pret
CREATE INDEX idx_remboursements_id_pret ON remboursements(id_pret);
CREATE INDEX idx_amortissement_id_pret ON amortissement_pret(id_pret);

-- Donnees de test pour les types de remboursement
INSERT INTO types_remboursement (nom, actif) VALUES
   ('Annuite constante', TRUE),
   ('Amortissement constante', TRUE);

-- Donnees de test pour les modalites
INSERT INTO modalites (libelle, nombre_mois, actif) VALUES
   ('Mensuel', 1, TRUE),
   ('Trimestriel', 3, TRUE),
   ('Semestriel', 6, TRUE),
   ('Annuel', 12, TRUE);

-- Donnees de test pour les statuts de pret (sans 'En retard')
INSERT INTO statuts_pret (libelle, actif) VALUES
   ('En cours', TRUE),
   ('Rembourse', TRUE);

-- Donnees de test pour les methodes de remboursement
INSERT INTO methodes_remboursement (libelle, actif) VALUES
   ('Virement bancaire', TRUE),
   ('Prelevement automatique', TRUE),
   ('Cheque', TRUE),
   ('Especes', TRUE);

-- Donnees de test pour les statuts de remboursement (sans 'En attente')
INSERT INTO statuts_remboursement(libelle, actif) VALUES
   ('Paye a temps', TRUE),
   ('En retard', TRUE);
