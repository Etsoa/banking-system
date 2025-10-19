package com.example.centralizer.models.pretDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Pret {
    private Integer id;
    private Integer idClient;
    private BigDecimal montant;
    private Integer dureeMois;
    private Integer dureePeriode;
    private LocalDate dateDebut;
    private Integer idStatutPret;
    private Integer idModalite;
    private Integer idTypeRemboursement;

    // Constructors
    public Pret() {}

    public Pret(Integer id, Integer idClient, BigDecimal montant, Integer dureeMois,
                Integer dureePeriode, LocalDate dateDebut, Integer idStatutPret,
                Integer idModalite, Integer idTypeRemboursement) {
        this.id = id;
        this.idClient = idClient;
        this.montant = montant;
        this.dureeMois = dureeMois;
        this.dureePeriode = dureePeriode;
        this.dateDebut = dateDebut;
        this.idStatutPret = idStatutPret;
        this.idModalite = idModalite;
        this.idTypeRemboursement = idTypeRemboursement;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIdClient() { return idClient; }
    public void setIdClient(Integer idClient) { this.idClient = idClient; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public Integer getDureeMois() { return dureeMois; }
    public void setDureeMois(Integer dureeMois) { this.dureeMois = dureeMois; }

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

    // Propriétés calculées pour l'affichage
    public Integer getIdPret() { return id; }
    
    public Double getDureeAnne() { 
        return dureeMois != null ? dureeMois / 12.0 : 0.0; 
    }
    
    public Double getTauxInteret() { 
        // Taux d'intérêt par défaut - à récupérer depuis la base si disponible
        return 5.0; 
    }
}