CREATE DATABASE banking_system_pret ;
\c banking_system_pret;
CREATE TABLE types_remboursement(
   id_type_remboursement SERIAL,
   nom VARCHAR(50)  NOT NULL,
   actif BOOLEAN NOT NULL default true,
   PRIMARY KEY(id_type_remboursement)
);

CREATE TABLE modalites(
   id_modalite SERIAL,
   libelle VARCHAR(50)  NOT NULL,
   nombre_mois VARCHAR(50)  NOT NULL,
   actif BOOLEAN NOT NULL default true,
   PRIMARY KEY(id_modalite)
);

CREATE TABLE statuts_pret(
   id_statut_pret SERIAL,
   libelle VARCHAR(50)  NOT NULL,
   actif BOOLEAN NOT NULL default true,
   PRIMARY KEY(id_statut_pret)
);

CREATE TABLE methodes_remboursement(
   id_methode_remboursement SERIAL,
   libelle VARCHAR(50)  NOT NULL,
   actif BOOLEAN NOT NULL default true,
   PRIMARY KEY(id_methode_remboursement)
);

CREATE TABLE prets(
   id_pret SERIAL,
   montant NUMERIC(12,2)   NOT NULL,
   duree_mois INTEGER NOT NULL,
   date_debut TIMESTAMP NOT NULL,
   id_statut_pret INTEGER NOT NULL,
   id_modalite INTEGER NOT NULL,
   id_type_remboursement INTEGER NOT NULL,
   PRIMARY KEY(id_pret),
   FOREIGN KEY(id_statut_pret) REFERENCES statuts_pret(id_statut_pret) on delete cascade on update cascade,
   FOREIGN KEY(id_modalite) REFERENCES modalites(id_modalite) on delete cascade on update cascade,
   FOREIGN KEY(id_type_remboursement) REFERENCES types_remboursement(id_type_remboursement) on delete cascade on update cascade
);

CREATE TABLE remboursements(
   id_remboursement INTEGER,
   date_paiement TIMESTAMP NOT NULL,
   montant NUMERIC(12,2)   NOT NULL,
   id_methode_remboursement INTEGER NOT NULL,
   id_pret INTEGER NOT NULL,
   PRIMARY KEY(id_remboursement),
   FOREIGN KEY(id_methode_remboursement) REFERENCES methodes_remboursement(id_methode_remboursement) on delete cascade on update cascade,
   FOREIGN KEY(id_pret) REFERENCES prets(id_pret) on delete cascade on update cascade
);

-- Données de test pour les types de remboursement
INSERT INTO types_remboursement (nom, actif) VALUES
   ('Mensuel', TRUE),
   ('Trimestriel', TRUE),
   ('Semestriel', TRUE),
   ('Annuel', TRUE);

-- Données de test pour les modalités
INSERT INTO modalites (libelle, nombre_mois, actif) VALUES
   ('Court terme', '12', TRUE),
   ('Moyen terme', '36', TRUE),
   ('Long terme', '60', TRUE),
   ('Très long terme', '120', TRUE);

-- Données de test pour les statuts de prêt
INSERT INTO statuts_pret (libelle, actif) VALUES
   ('En cours', TRUE),
   ('Terminé', TRUE),
   ('En retard', TRUE),
   ('Suspendu', TRUE),
   ('Annulé', TRUE);

-- Données de test pour les méthodes de remboursement
INSERT INTO methodes_remboursement (libelle, actif) VALUES
   ('Virement bancaire', TRUE),
   ('Prélèvement automatique', TRUE),
   ('Chèque', TRUE),
   ('Espèces', TRUE),
   ('Carte bancaire', TRUE);

-- Données de test pour les prêts
INSERT INTO prets (montant, duree_mois, date_debut, id_statut_pret, id_modalite, id_type_remboursement) VALUES
   (50000.00, 36, '2024-01-15 10:00:00', 1, 2, 1), -- Prêt de 50k€ sur 36 mois, en cours, remboursement mensuel
   (25000.00, 12, '2024-06-01 14:30:00', 1, 1, 1), -- Prêt de 25k€ sur 12 mois, en cours, remboursement mensuel
   (100000.00, 60, '2023-03-10 09:00:00', 2, 3, 1), -- Prêt de 100k€ sur 60 mois, terminé, remboursement mensuel
   (15000.00, 24, '2024-09-01 11:00:00', 3, 1, 2); -- Prêt de 15k€ sur 24 mois, en retard, remboursement trimestriel

-- Données de test pour les remboursements
INSERT INTO remboursements (id_remboursement, date_paiement, montant, id_methode_remboursement, id_pret) VALUES
   -- Remboursements pour le prêt 1 (50k€)
   (1, '2024-02-15 10:00:00', 1500.00, 2, 1),
   (2, '2024-03-15 10:00:00', 1500.00, 2, 1),
   (3, '2024-04-15 10:00:00', 1500.00, 2, 1),
   (4, '2024-05-15 10:00:00', 1500.00, 2, 1),
   
   -- Remboursements pour le prêt 2 (25k€)
   (5, '2024-07-01 14:30:00', 2200.00, 1, 2),
   (6, '2024-08-01 14:30:00', 2200.00, 1, 2),
   (7, '2024-09-01 14:30:00', 2200.00, 1, 2),
   
   -- Remboursements pour le prêt 3 (100k€ - terminé)
   (8, '2023-04-10 09:00:00', 1800.00, 2, 3),
   (9, '2023-05-10 09:00:00', 1800.00, 2, 3),
   (10, '2023-06-10 09:00:00', 1800.00, 2, 3),
   
   -- Remboursements pour le prêt 4 (15k€ - en retard)
   (11, '2024-12-01 11:00:00', 2000.00, 3, 4);
