package com.example.centralizer.repository;

import com.example.centralizer.models.HistoriqueRevenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriqueRevenuRepository extends JpaRepository<HistoriqueRevenu, Integer> {

    /**
     * Récupère tous les historiques de revenus d'un client, triés par date décroissante
     */
    List<HistoriqueRevenu> findByIdClientOrderByDateChangementDesc(Integer idClient);

    /**
     * Récupère le revenu actuel (le plus récent) d'un client
     */
    @Query("SELECT h FROM HistoriqueRevenu h WHERE h.idClient = :idClient " +
           "ORDER BY h.dateChangement DESC LIMIT 1")
    Optional<HistoriqueRevenu> findCurrentRevenuByClientId(@Param("idClient") Integer idClient);

    /**
     * Récupère la valeur du revenu actuel d'un client
     */
    @Query("SELECT h.valeur FROM HistoriqueRevenu h WHERE h.idClient = :idClient " +
           "ORDER BY h.dateChangement DESC LIMIT 1")
    Optional<BigDecimal> findCurrentRevenuValueByClientId(@Param("idClient") Integer idClient);

    /**
     * Vérifie si un client a un historique de revenus
     */
    boolean existsByIdClient(Integer idClient);
}