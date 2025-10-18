-- Creation de la base
CREATE DATABASE banking_system_pret;
\c banking_system_pret;

-- Table des types de remboursement
CREATE TABLE types_remboursement(
   id_type_remboursement SERIAL PRIMARY KEY,
   nom VARCHAR(50) NOT NULL,
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

-- Table des modalites
CREATE TABLE modalites(
   id_modalite SERIAL PRIMARY KEY,
   libelle VARCHAR(50) NOT NULL,
   nombre_mois INTEGER NOT NULL,  -- mois entre chaque paiement
   actif BOOLEAN NOT NULL DEFAULT TRUE
);

-- Table pour definir la plage de montant et duree en mois
CREATE TABLE plage_duree_pret(
    id_plage SERIAL PRIMARY KEY,
    montant_min NUMERIC(12,2) NOT NULL,
    montant_max NUMERIC(12,2) NOT NULL,
    duree_min_mois INTEGER NOT NULL,
    duree_max_mois INTEGER NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

-- Table des prets
CREATE TABLE prets(
   id_pret SERIAL PRIMARY KEY,
   id_client VARCHAR(50) NOT NULL,    -- id_client en string
   montant NUMERIC(12,2) NOT NULL,
   duree_mois INTEGER NOT NULL,       -- duree totale en mois
   duree_periode INTEGER NOT NULL,    -- nombre de periodes calculees selon modalite
   date_debut DATE NOT NULL,
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

-- Tableau d'amortissement
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

-- Table pour stocker le taux d'interet d'un pret
CREATE TABLE taux_interet (
    id_taux SERIAL PRIMARY KEY,
    taux_annuel NUMERIC(5,2) NOT NULL,
    date_debut DATE NOT NULL
);

CREATE TABLE frais(
   id_frais SERIAL,
   date_debut TIMESTAMP NOT NULL,
   nom VARCHAR(50)  NOT NULL,
   valeur INTEGER  NOT NULL,
   PRIMARY KEY(id_frais)
);

-- INDEX pour accelerer les recherches par id_pret
CREATE INDEX idx_remboursements_id_pret ON remboursements(id_pret);
CREATE INDEX idx_amortissement_id_pret ON amortissement_pret(id_pret);

-- Donnees exemple pour types de remboursement
INSERT INTO types_remboursement (nom, actif) VALUES
   ('Annuite constante', TRUE),
   ('Amortissement constante', TRUE);

-- Donnees exemple pour statuts de pret
INSERT INTO statuts_pret (libelle, actif) VALUES
   ('En cours', TRUE),
   ('Rembourse', TRUE);

-- Donnees exemple pour methodes de remboursement
INSERT INTO methodes_remboursement (libelle, actif) VALUES
   ('Virement bancaire', TRUE),
   ('Prelevement automatique', TRUE),
   ('Cheque', TRUE),
   ('Especes', TRUE);

-- Donnees exemple pour statuts de remboursement
INSERT INTO statuts_remboursement(libelle, actif) VALUES
   ('Paye a temps', TRUE),
   ('En retard', TRUE);

-- Donnees exemple pour modalites
INSERT INTO modalites(libelle, nombre_mois, actif) VALUES
   ('Mensuel', 1, TRUE),
   ('Trimestriel', 3, TRUE),
   ('Semestriel', 6, TRUE),
   ('Annuel', 12, TRUE);

-- Donnees exemple pour plage de montant et duree
INSERT INTO plage_duree_pret (montant_min, montant_max, duree_min_mois, duree_max_mois, actif) VALUES
   (0, 1000, 6, 12, TRUE),
   (1001, 5000, 12, 24, TRUE),
   (5001, 20000, 24, 36, TRUE),
   (20001, 100000, 36, 60, TRUE);

-- Donnees exemple pour taux d'interet
INSERT INTO taux_interet (taux_annuel, date_debut) VALUES
   (3.50, '2024-01-01'),
   (4.25, '2024-06-01'),
   (5.00, '2024-12-01'),
   (4.75, '2025-01-01');

-- Donnees exemple pour frais
INSERT INTO frais (date_debut, nom, valeur) VALUES
   ('2024-01-01 00:00:00', 'Frais de dossier', 150),
   ('2024-01-01 00:00:00', 'Assurance emprunteur', 500),
   ('2024-01-01 00:00:00', 'Frais de garantie', 300),
   ('2024-01-01 00:00:00', 'Frais de notaire', 1200),
   ('2024-06-01 00:00:00', 'Frais de retard', 50),
   ('2024-06-01 00:00:00', 'Penalite remboursement anticipe', 1000);
