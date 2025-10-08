CREATE DATABASE banking_system_pret ;
\c banking_system_pret;
CREATE TABLE types_remboursement(
   Id_type_remboursement SERIAL,
   nom VARCHAR(50)  NOT NULL,
   actif BOOLEAN NOT NULL default true,
   PRIMARY KEY(Id_type_remboursement)
);

CREATE TABLE modalites(
   id_modalite SERIAL,
   libelle VARCHAR(50)  NOT NULL,
   nombre_mois VARCHAR(50)  NOT NULL,
   actif BOOLEAN NOT NULL default true,
   PRIMARY KEY(id_modalite)
);

CREATE TABLE statuts_pret(
   Id_statut_pret SERIAL,
   libelle VARCHAR(50)  NOT NULL,
   actif BOOLEAN NOT NULL default true,
   PRIMARY KEY(Id_statut_pret)
);

CREATE TABLE methodes_remboursement(
   Id_methode_remboursement SERIAL,
   libelle VARCHAR(50)  NOT NULL,
   actif BOOLEAN NOT NULL default true,
   PRIMARY KEY(Id_methode_remboursement)
);

CREATE TABLE prets(
   id_pret SERIAL,
   montant NUMERIC(12,2)   NOT NULL,
   duree_mois INTEGER NOT NULL,
   date_debut TIMESTAMP NOT NULL,
   Id_statut_pret INTEGER NOT NULL,
   id_modalite INTEGER NOT NULL,
   Id_type_remboursement INTEGER NOT NULL,
   PRIMARY KEY(id_pret),
   FOREIGN KEY(Id_statut_pret) REFERENCES statuts_pret(Id_statut_pret) on delete cascade on update cascade,
   FOREIGN KEY(id_modalite) REFERENCES modalites(id_modalite) on delete cascade on update cascade,
   FOREIGN KEY(Id_type_remboursement) REFERENCES types_remboursement(Id_type_remboursement) on delete cascade on update cascade
);

CREATE TABLE remboursements(
   id_remboursement INTEGER,
   date_paiement TIMESTAMP NOT NULL,
   montant NUMERIC(12,2)   NOT NULL,
   Id_methode_remboursement INTEGER NOT NULL,
   id_pret INTEGER NOT NULL,
   PRIMARY KEY(id_remboursement),
   FOREIGN KEY(Id_methode_remboursement) REFERENCES methodes_remboursement(Id_methode_remboursement) on delete cascade on update cascade,
   FOREIGN KEY(id_pret) REFERENCES prets(id_pret) on delete cascade on update cascade
);
