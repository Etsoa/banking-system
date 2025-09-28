CREATE TABLE Clients(
   id_client INTEGER,
   nom VARCHAR(50)  NOT NULL,
   email VARCHAR(50)  NOT NULL,
   adresse VARCHAR(50)  NOT NULL,
   contact VARCHAR(50)  NOT NULL,
   date_naissance DATE NOT NULL,
   PRIMARY KEY(id_client)
);

CREATE TABLE type_compte(
   libelle VARCHAR(50) ,
   id_type_compte INTEGER NOT NULL,
   PRIMARY KEY(libelle)
);

CREATE TABLE comptes(
   id_compte INTEGER,
   id_client INTEGER NOT NULL,
   libelle VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id_compte),
   FOREIGN KEY(id_client) REFERENCES Clients(id_client),
   FOREIGN KEY(libelle) REFERENCES type_compte(libelle)
);

CREATE TABLE type_statu_compte(
   id_type_statu_compte INTEGER,
   libelle VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id_type_statu_compte)
);

CREATE TABLE type_transactions(
   id_type_transaction INTEGER,
   libelle VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id_type_transaction)
);

CREATE TABLE transferts(
   id_transfert INTEGER,
   date_transfert TIMESTAMP NOT NULL,
   montant BIGINT NOT NULL,
   id_compte_envoyeur INTEGER NOT NULL,
   id_compte_receveur INTEGER NOT NULL,
   PRIMARY KEY(id_transfert),
   FOREIGN KEY(id_compte_envoyeur) REFERENCES comptes(id_compte),
   FOREIGN KEY(id_compte_receveur) REFERENCES comptes(id_compte)
);

CREATE TABLE historique_statu_compte(
   id_historique_statu_compte INTEGER,
   date_statu_compte TIMESTAMP NOT NULL,
   id_type_statu_compte INTEGER NOT NULL,
   id_compte INTEGER NOT NULL,
   PRIMARY KEY(id_historique_statu_compte),
   FOREIGN KEY(id_type_statu_compte) REFERENCES type_statu_compte(id_type_statu_compte),
   FOREIGN KEY(id_compte) REFERENCES comptes(id_compte)
);

CREATE TABLE modalites(
   id_modalite INTEGER,
   libelle INTEGER NOT NULL,
   nb_mois INTEGER NOT NULL,
   PRIMARY KEY(id_modalite)
);

CREATE TABLE type_remboursement(
   id_type_remboursement INTEGER,
   libelle VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id_type_remboursement)
);

CREATE TABLE type_statu_pret(
   id_type_statu_pret INTEGER,
   libelle VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id_type_statu_pret)
);

CREATE TABLE methode_remboursement(
   id_methode_remboursement INTEGER,
   libelle VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id_methode_remboursement)
);

CREATE TABLE historique_solde(
   id_historique_solde INTEGER,
   montant BIGINT NOT NULL,
   date_changement TIMESTAMP NOT NULL,
   id_compte INTEGER NOT NULL,
   PRIMARY KEY(id_historique_solde),
   FOREIGN KEY(id_compte) REFERENCES comptes(id_compte)
);

CREATE TABLE historique_taux(
   id_historique_tot INTEGER,
   date_debut DATE NOT NULL,
   valeur INTEGER NOT NULL,
   nom VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id_historique_tot)
);

CREATE TABLE transactions(
   id_transaction INTEGER,
   montant BIGINT NOT NULL,
   id_compte INTEGER NOT NULL,
   id_type_transaction INTEGER NOT NULL,
   PRIMARY KEY(id_transaction),
   FOREIGN KEY(id_compte) REFERENCES comptes(id_compte),
   FOREIGN KEY(id_type_transaction) REFERENCES type_transactions(id_type_transaction)
);

CREATE TABLE prets(
   id_pret INTEGER,
   date_pret TIMESTAMP NOT NULL,
   montant BIGINT NOT NULL,
   nb_mois_retour_prevu INTEGER NOT NULL,
   id_type_statu_pret INTEGER NOT NULL,
   id_type_remboursement INTEGER NOT NULL,
   id_modalite INTEGER NOT NULL,
   id_client INTEGER NOT NULL,
   PRIMARY KEY(id_pret),
   FOREIGN KEY(id_type_statu_pret) REFERENCES type_statu_pret(id_type_statu_pret),
   FOREIGN KEY(id_type_remboursement) REFERENCES type_remboursement(id_type_remboursement),
   FOREIGN KEY(id_modalite) REFERENCES modalites(id_modalite),
   FOREIGN KEY(id_client) REFERENCES Clients(id_client)
);

CREATE TABLE remboursements(
   id_remboursement INTEGER,
   montant BIGINT NOT NULL,
   date_remboursement TIMESTAMP NOT NULL,
   id_methode_remboursement INTEGER NOT NULL,
   id_pret INTEGER NOT NULL,
   PRIMARY KEY(id_remboursement),
   FOREIGN KEY(id_methode_remboursement) REFERENCES methode_remboursement(id_methode_remboursement),
   FOREIGN KEY(id_pret) REFERENCES prets(id_pret)
);
