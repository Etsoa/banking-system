package com.example.centralizer.models.pretDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Pret {
    private Integer idPret;
    private Integer idClient;
    private BigDecimal montant;
    private BigDecimal tauxInteret;
    private Integer dureeAnne;
    private LocalDate dateDebut;
    private Integer idMethodeRemboursement;
    private Integer idModalite;
    private Integer idStatutPret;

    // Constructors
    public Pret() {}

    public Pret(Integer idPret, Integer idClient, BigDecimal montant, BigDecimal tauxInteret, 
                Integer dureeAnne, LocalDate dateDebut, Integer idMethodeRemboursement, 
                Integer idModalite, Integer idStatutPret) {
        this.idPret = idPret;
        this.idClient = idClient;
        this.montant = montant;
        this.tauxInteret = tauxInteret;
        this.dureeAnne = dureeAnne;
        this.dateDebut = dateDebut;
        this.idMethodeRemboursement = idMethodeRemboursement;
        this.idModalite = idModalite;
        this.idStatutPret = idStatutPret;
    }

    // Getters & Setters
    public Integer getIdPret() { return idPret; }
    public void setIdPret(Integer idPret) { this.idPret = idPret; }

    public Integer getIdClient() { return idClient; }
    public void setIdClient(Integer idClient) { this.idClient = idClient; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public BigDecimal getTauxInteret() { return tauxInteret; }
    public void setTauxInteret(BigDecimal tauxInteret) { this.tauxInteret = tauxInteret; }

    public Integer getDureeAnne() { return dureeAnne; }
    public void setDureeAnne(Integer dureeAnne) { this.dureeAnne = dureeAnne; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public Integer getIdMethodeRemboursement() { return idMethodeRemboursement; }
    public void setIdMethodeRemboursement(Integer idMethodeRemboursement) { this.idMethodeRemboursement = idMethodeRemboursement; }

    public Integer getIdModalite() { return idModalite; }
    public void setIdModalite(Integer idModalite) { this.idModalite = idModalite; }

    public Integer getIdStatutPret() { return idStatutPret; }
    public void setIdStatutPret(Integer idStatutPret) { this.idStatutPret = idStatutPret; }
}