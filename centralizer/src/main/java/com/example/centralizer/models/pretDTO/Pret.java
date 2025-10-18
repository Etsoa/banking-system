package com.example.centralizer.models.pretDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Pret {
    private Integer id;
    private String clientId;
    private BigDecimal montant;
    private Integer dureePeriode;
    private LocalDate dateDebut;
    private Integer idStatutPret;
    private Integer idModalite;
    private Integer idTypeRemboursement;

    // Constructors
    public Pret() {}

    public Pret(Integer id, String clientId, BigDecimal montant, 
                Integer dureePeriode, LocalDate dateDebut, Integer idStatutPret,
                Integer idModalite, Integer idTypeRemboursement) {
        this.id = id;
        this.clientId = clientId;
        this.montant = montant;
        this.dureePeriode = dureePeriode;
        this.dateDebut = dateDebut;
        this.idStatutPret = idStatutPret;
        this.idModalite = idModalite;
        this.idTypeRemboursement = idTypeRemboursement;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public Integer getDureePeriode() { return dureePeriode; }
    public void setDureePeriode(Integer dureePeriode) { this.dureePeriode = dureePeriode; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public Integer getIdStatutPret() { return idStatutPret; }
    public void setIdStatutPret(Integer idStatutPret) { this.idStatutPret = idStatutPret; }

    public Integer getIdModalite() { return idModalite; }
    public void setIdModalite(Integer idModalite) { this.idModalite = idModalite; }

    public Integer getIdTypeRemboursement() { return idTypeRemboursement; }
    public void setIdTypeRemboursement(Integer idTypeRemboursement) { this.idTypeRemboursement = idTypeRemboursement; }
}