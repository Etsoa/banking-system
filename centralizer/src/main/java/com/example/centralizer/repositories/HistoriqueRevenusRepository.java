package com.example.centralizer.repositories;

import com.example.centralizer.models.HistoriqueRevenus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HistoriqueRevenusRepository extends JpaRepository<HistoriqueRevenus, Integer> {

    /**
     * Trouve le revenu le plus récent d'un client à une date donnée
     */
    @Query("SELECT hr FROM HistoriqueRevenus hr WHERE hr.idClient = :idClient " +
           "AND hr.periodeDebut <= :dateReference " +
           "AND hr.periodeFin >= :dateReference " +
           "ORDER BY hr.dateEnregistrement DESC")
    Optional<HistoriqueRevenus> findCurrentRevenuByClientAndDate(@Param("idClient") Integer idClient, 
                                                                @Param("dateReference") LocalDate dateReference);

    /**
     * Trouve le revenu le plus récent d'un client (sans contrainte de date)
     */
    @Query("SELECT hr FROM HistoriqueRevenus hr WHERE hr.idClient = :idClient " +
           "ORDER BY hr.dateEnregistrement DESC")
    Optional<HistoriqueRevenus> findLatestRevenuByClient(@Param("idClient") Integer idClient);
}