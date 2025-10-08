package com.example.centralizer.repository;

import com.example.centralizer.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    Optional<Utilisateur> findByNomUtilisateur(String nomUtilisateur);
    
    Optional<Utilisateur> findByEmail(String email);
    
    List<Utilisateur> findByStatut(String statut);
    
    @Query("SELECT u FROM Utilisateur u WHERE u.dateCreation BETWEEN :startDate AND :endDate")
    List<Utilisateur> findByDateCreationBetween(@Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT u FROM Utilisateur u WHERE u.nomUtilisateur = :nomUtilisateur AND u.motDePasse = :motDePasse")
    Optional<Utilisateur> findByNomUtilisateurAndMotDePasse(@Param("nomUtilisateur") String nomUtilisateur, 
                                                            @Param("motDePasse") String motDePasse);
    
    boolean existsByNomUtilisateur(String nomUtilisateur);
    
    boolean existsByEmail(String email);
}