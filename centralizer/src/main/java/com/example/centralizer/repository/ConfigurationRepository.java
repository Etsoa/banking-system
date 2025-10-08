package com.example.centralizer.repository;

import com.example.centralizer.models.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Integer> {
    
    /**
     * Recherche une configuration par sa clé
     */
    Optional<Configuration> findByCle(String cle);
    
    /**
     * Vérifie si une configuration existe par sa clé
     */
    boolean existsByCle(String cle);
    
    /**
     * Supprime une configuration par sa clé
     */
    void deleteByCle(String cle);
}