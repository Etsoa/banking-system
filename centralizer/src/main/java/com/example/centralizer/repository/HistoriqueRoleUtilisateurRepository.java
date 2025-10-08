package com.example.centralizer.repository;

import com.example.centralizer.models.HistoriqueRoleUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistoriqueRoleUtilisateurRepository extends JpaRepository<HistoriqueRoleUtilisateur, Integer> {
    
    /**
     * Trouve tous les rôles d'un utilisateur
     */
    List<HistoriqueRoleUtilisateur> findByIdUtilisateur(Integer idUtilisateur);
    
    /**
     * Trouve les rôles actifs d'un utilisateur (non révoqués)
     */
    @Query("SELECT h FROM HistoriqueRoleUtilisateur h WHERE h.idUtilisateur = :idUtilisateur AND h.dateRevocation IS NULL")
    List<HistoriqueRoleUtilisateur> findActiveRolesByUtilisateur(@Param("idUtilisateur") Integer idUtilisateur);
    
    /**
     * Trouve l'historique d'un rôle spécifique
     */
    List<HistoriqueRoleUtilisateur> findByIdRole(Integer idRole);
    
    /**
     * Trouve les attributions par utilisateur attribuant
     */
    List<HistoriqueRoleUtilisateur> findByAttribuePar(Integer attribuePar);
    
    /**
     * Trouve les attributions dans une période
     */
    @Query("SELECT h FROM HistoriqueRoleUtilisateur h WHERE h.dateAttribution BETWEEN :debut AND :fin")
    List<HistoriqueRoleUtilisateur> findByDateAttributionBetween(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}