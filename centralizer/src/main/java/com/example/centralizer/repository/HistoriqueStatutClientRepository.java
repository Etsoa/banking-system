package com.example.centralizer.repository;

import com.example.centralizer.models.HistoriqueStatutClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistoriqueStatutClientRepository extends JpaRepository<HistoriqueStatutClient, Integer> {
    
    // Trouver tous les historiques d'un client spécifique
    List<HistoriqueStatutClient> findByIdClientOrderByDateChangementDesc(Integer idClient);
    
    // Trouver tous les clients ayant un statut spécifique
    List<HistoriqueStatutClient> findByStatutOrderByDateChangementDesc(String statut);
    
    // Trouver le dernier statut d'un client
    @Query("SELECT h FROM HistoriqueStatutClient h WHERE h.idClient = :idClient ORDER BY h.dateChangement DESC")
    List<HistoriqueStatutClient> findLatestStatusByClientId(@Param("idClient") Integer idClient);
    
    // Trouver les changements de statut dans une période donnée
    List<HistoriqueStatutClient> findByDateChangementBetweenOrderByDateChangementDesc(
            LocalDateTime dateDebut, LocalDateTime dateFin);
    
    // Trouver les changements de statut d'un client dans une période donnée
    List<HistoriqueStatutClient> findByIdClientAndDateChangementBetweenOrderByDateChangementDesc(
            Integer idClient, LocalDateTime dateDebut, LocalDateTime dateFin);
    
    // Compter le nombre de changements de statut pour un client
    long countByIdClient(Integer idClient);
    
    // Compter le nombre de clients ayant un statut spécifique
    @Query("SELECT COUNT(DISTINCT h.idClient) FROM HistoriqueStatutClient h WHERE h.statut = :statut")
    long countDistinctClientsByStatut(@Param("statut") String statut);
}