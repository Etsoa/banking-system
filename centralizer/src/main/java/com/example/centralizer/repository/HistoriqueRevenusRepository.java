package com.example.centralizer.repository;

import com.example.centralizer.models.HistoriqueRevenus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface HistoriqueRevenusRepository extends JpaRepository<HistoriqueRevenus, Integer> {
    
    /**
     * Trouve tous les revenus d'un client
     */
    List<HistoriqueRevenus> findByIdClient(Integer idClient);
    
    /**
     * Trouve les revenus d'un client ordonnés par date d'enregistrement
     */
    List<HistoriqueRevenus> findByIdClientOrderByDateEnregistrementDesc(Integer idClient);
    
    /**
     * Trouve les revenus par source
     */
    List<HistoriqueRevenus> findBySourceRevenus(String sourceRevenus);
    
    /**
     * Trouve les revenus dans une période d'enregistrement
     */
    @Query("SELECT h FROM HistoriqueRevenus h WHERE h.dateEnregistrement BETWEEN :debut AND :fin")
    List<HistoriqueRevenus> findByDateEnregistrementBetween(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
    
    /**
     * Trouve les revenus d'un client pour une période spécifique
     */
    @Query("SELECT h FROM HistoriqueRevenus h WHERE h.idClient = :idClient AND h.periodeDebut >= :debut AND h.periodeFin <= :fin")
    List<HistoriqueRevenus> findByClientAndPeriode(@Param("idClient") Integer idClient, 
                                                  @Param("debut") LocalDate debut, 
                                                  @Param("fin") LocalDate fin);
    
    /**
     * Calcule le total des revenus d'un client
     */
    @Query("SELECT SUM(h.montantRevenus) FROM HistoriqueRevenus h WHERE h.idClient = :idClient")
    BigDecimal getTotalRevenusByClient(@Param("idClient") Integer idClient);
    
    /**
     * Trouve les revenus supérieurs à un montant
     */
    List<HistoriqueRevenus> findByMontantRevenusGreaterThan(BigDecimal montant);
}