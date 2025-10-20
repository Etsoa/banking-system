CREATE DATABASE banking_system_compte_courant ;
\c banking_system_compte_courant;
CREATE SEQUENCE compte_seq START 1;

CREATE TABLE comptes(
   id_num INTEGER DEFAULT nextval('compte_seq'),
   id_compte VARCHAR(10) GENERATED ALWAYS AS ('C' || id_num) STORED,
   date_ouverture TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   decouvert BOOLEAN NOT NULL,
   id_client INTEGER NOT NULL,
   solde NUMERIC(12,2) NOT NULL DEFAULT 0,
   PRIMARY KEY(id_compte)
);

CREATE TABLE types_transaction(
   id_type_transaction SERIAL,
   libelle VARCHAR(50)  NOT NULL,
   actif BOOLEAN NOT NULL DEFAULT TRUE,
   signe VARCHAR(1) NOT NULL,
   PRIMARY KEY(id_type_transaction)
);

CREATE TABLE transferts(
   id_transfert SERIAL,
   date_transfert DATE NOT NULL DEFAULT CURRENT_DATE,
   montant NUMERIC(12,2)   NOT NULL,
   id_transaction_envoyeur VARCHAR(10) NOT NULL,
   id_transaction_receveur VARCHAR(10) NOT NULL,
   envoyer VARCHAR(10) NOT NULL,
   receveur VARCHAR(10) NOT NULL,
   PRIMARY KEY(id_transfert),
   FOREIGN KEY(envoyer) REFERENCES comptes(id_compte) on delete cascade on update cascade,
   FOREIGN KEY(receveur) REFERENCES comptes(id_compte) on delete cascade on update cascade
);

CREATE TABLE transactions(
   id_transaction SERIAL,
   date_transaction TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   montant NUMERIC(12,2)   NOT NULL,
   id_type_transaction INTEGER NOT NULL,
   id_compte VARCHAR(10) NOT NULL,
   PRIMARY KEY(id_transaction),
   FOREIGN KEY(id_type_transaction) REFERENCES types_transaction(id_type_transaction) on delete cascade on update cascade,
   FOREIGN KEY(id_compte) REFERENCES comptes(id_compte) on delete cascade on update cascade
);

CREATE TABLE historiques_solde(
   id_historique_solde SERIAL,
   montant NUMERIC(12,2)   NOT NULL,
   date_changement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   id_compte VARCHAR(10) NOT NULL,
   id_transaction INTEGER NOT NULL,
   PRIMARY KEY(id_historique_solde),
   FOREIGN KEY(id_transaction) REFERENCES transactions(id_transaction) on delete cascade on update cascade,
   FOREIGN KEY(id_compte) REFERENCES comptes(id_compte) on delete cascade on update cascade
);

CREATE TABLE types_statut_compte(
   id_type_statut_compte SERIAL,
   libelle VARCHAR(50)  NOT NULL,
   actif BOOLEAN NOT NULL DEFAULT TRUE,
   PRIMARY KEY(id_type_statut_compte)
);

CREATE TABLE historiques_statut_compte(
   id_historique_statut_compte SERIAL,
   date_changement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   id_compte VARCHAR(10) NOT NULL,
   id_type_statut_compte INTEGER NOT NULL,
   PRIMARY KEY(id_historique_statut_compte),
   FOREIGN KEY(id_compte) REFERENCES comptes(id_compte) on delete cascade on update cascade,
   FOREIGN KEY(id_type_statut_compte) REFERENCES types_statut_compte(id_type_statut_compte) on delete cascade on update cascade
);

CREATE TABLE frais(
   id_frais SERIAL,
   date_debut TIMESTAMP NOT NULL,
   nom VARCHAR(50)  NOT NULL,
   montant_min NUMERIC(12,2)   NOT NULL,
   montant_max NUMERIC(12,2)   NOT NULL,
   valeur INTEGER  NOT NULL,
   PRIMARY KEY(id_frais)
);

CREATE TABLE decouverte(
   id_frais SERIAL,
   date_debut TIMESTAMP NOT NULL,
   revenu_min NUMERIC(12,2)   NOT NULL,
   revenu_max NUMERIC(12,2)   NOT NULL,
   valeur INTEGER  NOT NULL,
   PRIMARY KEY(id_frais)
);

-- Données pour les types de transaction
INSERT INTO types_transaction (libelle, actif, signe) VALUES
   ('Depot', TRUE, '+'),
   ('Retrait', TRUE, '-'),
   ('Virement entrant', TRUE, '+'),
   ('Virement sortant', TRUE, '-');

-- Données pour les types de statut de compte
INSERT INTO types_statut_compte (libelle, actif) VALUES
   ('Actif', TRUE),
   ('Bloqué', TRUE),
   ('Fermé', TRUE);

-- Données de test pour les comptes
INSERT INTO comptes (date_ouverture, decouvert, id_client, solde) VALUES
   ('2025-10-15 17:28:33.459', TRUE, 1, 10.00);

-- Données de test pour les historiques de statut (utilise l'id_compte généré)
INSERT INTO historiques_statut_compte (date_changement, id_compte, id_type_statut_compte) VALUES
   ('2025-10-15 17:28:33.459', 'C1', 1); -- Compte C1 est Actif


-- Données pour la table frais
INSERT INTO frais (date_debut, nom, montant_min, montant_max, valeur) VALUES
   ('2025-01-01 00:00:00', 'Frais de retrait', 0, 10000, 10),
   ('2025-01-01 00:00:00', 'Frais de retrait', 10001, 20000, 20),
   ('2025-01-01 00:00:00', 'Frais de retrait', 20001, 30000, 30),
   ('2025-01-01 00:00:00', 'Frais de virement sortant', 0, 5000, 5),
   ('2025-01-01 00:00:00', 'Frais de virement sortant', 5001, 15000, 10),
   ('2025-01-01 00:00:00', 'Frais de virement sortant', 15001, 50000, 15);

-- Données pour la table decouverte
INSERT INTO decouverte (date_debut, revenu_min, revenu_max, valeur) VALUES
   ('2025-01-01 00:00:00', 0, 1000, 50),
   ('2025-01-01 00:00:00', 1000, 5000, 100),
   ('2025-01-01 00:00:00', 5000, 10000, 200);
