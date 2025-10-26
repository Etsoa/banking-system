package com.example.comptecourant.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTransaction;
    private LocalDate dateTransaction;
    private BigDecimal montant;
    
    @ManyToOne
    @JoinColumn(name = "id_compte", foreignKey = @ForeignKey(name = "fk_transaction_compte"))
    private CompteCourant compte;
    
    @ManyToOne
    @JoinColumn(name = "id_compte_contrpartie", foreignKey = @ForeignKey(name = "fk_transaction_contrepartie"))
    private CompteCourant compteContrepartie;
    
    @Enumerated(EnumType.STRING)
    private TypeTransaction typeTransaction;
    
    @Enumerated(EnumType.STRING)
    private StatutTransaction statutTransaction;

    public Transaction() {}

    public Integer getIdTransaction() { return idTransaction; }
    public void setIdTransaction(Integer idTransaction) { this.idTransaction = idTransaction; }

    public LocalDate getDateTransaction() { return dateTransaction; }
    public void setDateTransaction(LocalDate dateTransaction) { this.dateTransaction = dateTransaction; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public CompteCourant getCompte() { return compte; }
    public void setCompte(CompteCourant compte) { this.compte = compte; }
    
    public Integer getIdCompte() { 
        return compte != null ? compte.getIdCompte() : null;
    }
    public void setIdCompte(Integer idCompte) {
        if (compte == null) compte = new CompteCourant();
        compte.setIdCompte(idCompte);
    }

    public CompteCourant getCompteContrepartie() { return compteContrepartie; }
    public void setCompteContrepartie(CompteCourant compteContrepartie) { this.compteContrepartie = compteContrepartie; }

    public TypeTransaction getTypeTransaction() { return typeTransaction; }
    public void setTypeTransaction(TypeTransaction typeTransaction) { this.typeTransaction = typeTransaction; }

    public StatutTransaction getStatutTransaction() { return statutTransaction; }
    public void setStatutTransaction(StatutTransaction statutTransaction) { this.statutTransaction = statutTransaction; }
}
