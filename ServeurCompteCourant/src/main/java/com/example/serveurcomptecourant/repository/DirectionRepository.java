package com.example.serveurcomptecourant.repository;

import java.util.List;

import com.example.serveurcomptecourant.models.Direction;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class DirectionRepository {
    @PersistenceContext(unitName = "ServeurCompteCourantPU")
    private EntityManager em;

    public Direction save(Direction direction) {
        if (direction.getIdDirection() == null) {
            em.persist(direction);
            return direction;
        } else {
            return em.merge(direction);
        }
    }

    public Direction find(Integer idDirection) {
        return em.find(Direction.class, idDirection);
    }

    public List<Direction> findAll() {
        return em.createQuery("SELECT d FROM Direction d ORDER BY d.niveau", Direction.class).getResultList();
    }

    public List<Direction> findByNiveau(Integer niveau) {
        return em.createQuery("SELECT d FROM Direction d WHERE d.niveau = :niveau", Direction.class)
                .setParameter("niveau", niveau)
                .getResultList();
    }

    public void delete(Direction direction) {
        em.remove(em.contains(direction) ? direction : em.merge(direction));
    }
}