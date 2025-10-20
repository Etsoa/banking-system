package com.example.serveurcomptecourant.services;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.serveurcomptecourant.models.Direction;
import com.example.serveurcomptecourant.repository.DirectionRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class DirectionService {
    private static final Logger LOGGER = Logger.getLogger(DirectionService.class.getName());
    
    @EJB
    private DirectionRepository directionRepository;

    /**
     * Récupère toutes les directions
     */
    public List<Direction> getAllDirections() {
        try {
            return directionRepository.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de toutes les directions", e);
            throw new RuntimeException("Erreur lors de la récupération des directions", e);
        }
    }

    /**
     * Récupère une direction par ID
     */
    public Direction getDirectionById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID de la direction est obligatoire");
        }
        
        try {
            Direction direction = directionRepository.find(id);
            if (direction == null) {
                throw new IllegalArgumentException("Direction introuvable avec ID: " + id);
            }
            return direction;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de la direction " + id, e);
            throw new RuntimeException("Erreur lors de la récupération de la direction", e);
        }
    }

    /**
     * Crée une nouvelle direction
     */
    public Direction createDirection(Direction direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Les données de la direction sont obligatoires");
        }
        
        validateDirectionData(direction);
        
        try {
            return directionRepository.save(direction);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de la direction", e);
            throw new RuntimeException("Erreur lors de la création de la direction", e);
        }
    }

    /**
     * Met à jour une direction
     */
    public Direction updateDirection(Direction direction) {
        if (direction == null || direction.getIdDirection() == null) {
            throw new IllegalArgumentException("La direction et son ID sont obligatoires");
        }
        
        try {
            // Vérifier que la direction existe
            Direction existante = directionRepository.find(direction.getIdDirection());
            if (existante == null) {
                throw new IllegalArgumentException("Direction introuvable avec ID: " + direction.getIdDirection());
            }
            
            validateDirectionData(direction);
            
            return directionRepository.save(direction);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de la direction " + direction.getIdDirection(), e);
            throw new RuntimeException("Erreur lors de la mise à jour de la direction", e);
        }
    }

    /**
     * Supprime une direction
     */
    public void deleteDirection(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID de la direction est obligatoire");
        }
        
        try {
            Direction direction = directionRepository.find(id);
            if (direction == null) {
                throw new IllegalArgumentException("Direction introuvable avec ID: " + id);
            }
            
            directionRepository.delete(direction);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de la direction " + id, e);
            throw new RuntimeException("Erreur lors de la suppression de la direction", e);
        }
    }

    /**
     * Validation des données de direction
     */
    private void validateDirectionData(Direction direction) {
        if (direction.getLibelle() == null || direction.getLibelle().trim().isEmpty()) {
            throw new IllegalArgumentException("Le libellé de la direction est obligatoire");
        }
        
        if (direction.getNiveau() == null || direction.getNiveau() < 0) {
            throw new IllegalArgumentException("Le niveau de la direction est obligatoire et doit être positif");
        }
    }
}