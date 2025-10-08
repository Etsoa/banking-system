package com.example.centralizer.repository;

import com.example.centralizer.models.HistoriqueTaux;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriqueTauxRepository extends JpaRepository<HistoriqueTaux, Integer> {

    List<HistoriqueTaux> findByActifTrue();
    
    List<HistoriqueTaux> findByNom(String nom);
    
    @Query("SELECT h FROM HistoriqueTaux h WHERE h.nom = :nom AND h.actif = true ORDER BY h.dateDebut DESC")
    Optional<HistoriqueTaux> findLatestByNomAndActif(@Param("nom") String nom);
    
    @Query("SELECT h FROM HistoriqueTaux h WHERE h.dateDebut BETWEEN :startDate AND :endDate")
    List<HistoriqueTaux> findByDateDebutBetween(@Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT h FROM HistoriqueTaux h WHERE h.dateDebut <= :date AND h.actif = true ORDER BY h.dateDebut DESC")
    List<HistoriqueTaux> findActiveRatesAsOfDate(@Param("date") LocalDateTime date);
}