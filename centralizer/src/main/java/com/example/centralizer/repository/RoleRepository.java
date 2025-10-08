package com.example.centralizer.repository;

import com.example.centralizer.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    
    /**
     * Recherche un rôle par son nom
     */
    Optional<Role> findByNom(String nom);
    
    /**
     * Vérifie si un rôle existe par son nom
     */
    boolean existsByNom(String nom);
}