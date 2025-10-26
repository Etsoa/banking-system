package com.example.echange.ejb;

import com.example.centralizer.dto.echange.Echange;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.ejb.Remote;

@Remote
public interface EchangeServiceRemote {
    List<Echange> getAllEchanges();
    void addEchange(Echange e);
    boolean removeEchangeByNom(String nom);
    void recharger();
    
    // Méthodes avec date obligatoire
    List<Echange> getEchangesActifs(LocalDate date);
    BigDecimal convertirVersAriary(String devise, BigDecimal montant, LocalDate date);
    BigDecimal convertirDepuisAriary(String devise, BigDecimal montantAriary, LocalDate date);
}
