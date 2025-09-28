package com.example.serveurcomptecourant.services;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class CompteCourantService {
    @PersistenceContext(unitName = "ServeurCompteCourantPU")
    private EntityManager em;

    // Business logic methods here
}
