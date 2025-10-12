CREATE database banking_system_compte_depot ;
\c banking_system_compte_depot;
CREATE TABLE comptes(
   id_compte SERIAL,
   date_ouverture TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
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
   envoyer INTEGER NOT NULL,
   receveur INTEGER NOT NULL,
   PRIMARY KEY(id_transfert),
   FOREIGN KEY(envoyer) REFERENCES comptes(id_compte) on delete cascade on update cascade,
   FOREIGN KEY(receveur) REFERENCES comptes(id_compte) on delete cascade on update cascade
);

CREATE TABLE transactions(
   id_transaction SERIAL,
   date_transaction TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   montant NUMERIC(12,2)   NOT NULL,
   id_type_transaction INTEGER NOT NULL,
   id_compte INTEGER NOT NULL,
   id_transfert INTEGER,
   PRIMARY KEY(id_transaction),
   FOREIGN KEY(id_type_transaction) REFERENCES types_transaction(id_type_transaction) on delete cascade on update cascade,
   FOREIGN KEY(id_transfert) REFERENCES transferts(id_transfert) on delete cascade on update cascade,
   FOREIGN KEY(id_compte) REFERENCES comptes(id_compte) on delete cascade on update cascade
);

CREATE TABLE historiques_solde(
   id_historique_solde SERIAL,
   montant NUMERIC(12,2)   NOT NULL,
   date_changement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   id_compte INTEGER NOT NULL,
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
   id_compte INTEGER NOT NULL,
   id_type_statut_compte INTEGER NOT NULL,
   PRIMARY KEY(id_historique_statut_compte),
   FOREIGN KEY(id_compte) REFERENCES comptes(id_compte) on delete cascade on update cascade,
   FOREIGN KEY(id_type_statut_compte) REFERENCES types_statut_compte(id_type_statut_compte) on delete cascade on update cascade
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
