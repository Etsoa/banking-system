package com.example.centralizer.repository;

import com.example.centralizer.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {

    Optional<Client> findByEmail(String email);
    
    List<Client> findByNomContainingIgnoreCase(String nom);
    
    List<Client> findByPrenomContainingIgnoreCase(String prenom);
    
    @Query("SELECT c FROM Client c WHERE LOWER(c.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Client> findByNomOrPrenomContaining(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT c FROM Client c WHERE c.telephone = :telephone")
    Optional<Client> findByTelephone(@Param("telephone") String telephone);
}