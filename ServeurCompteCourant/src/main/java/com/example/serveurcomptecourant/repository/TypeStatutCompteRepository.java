package com.example.serveurcomptecourant.repository;

import com.example.serveurcomptecourant.models.TypeStatutCompte;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class TypeStatutCompteRepository {

    @PersistenceContext(unitName = "ServeurCompteCourantPU")
    private EntityManager em;

    /**
     * Sauvegarde ou met à jour un type de statut de compte
     */
    public TypeStatutCompte save(TypeStatutCompte typeStatutCompte) {
        if (typeStatutCompte.getId() == null) {
            em.persist(typeStatutCompte);
            return typeStatutCompte;
        } else {
            return em.merge(typeStatutCompte);
        }
    }

    /**
     * Trouve un type de statut de compte par ID
     */
    public TypeStatutCompte findById(Integer id) {
        return em.find(TypeStatutCompte.class, id);
    }

    /**
     * Trouve tous les types de statut de compte
     */
    public List<TypeStatutCompte> findAll() {
        TypedQuery<TypeStatutCompte> query = em.createQuery(
            "SELECT t FROM TypeStatutCompte t ORDER BY t.libelle", 
            TypeStatutCompte.class
        );
        return query.getResultList();
    }

    /**
     * Trouve tous les types de statut de compte actifs
     */
    public List<TypeStatutCompte> findAllActifs() {
        TypedQuery<TypeStatutCompte> query = em.createQuery(
            "SELECT t FROM TypeStatutCompte t WHERE t.actif = true ORDER BY t.libelle", 
            TypeStatutCompte.class
        );
        return query.getResultList();
    }

    /**
     * Trouve un type de statut de compte par libellé
     */
    public TypeStatutCompte findByLibelle(String libelle) {
        TypedQuery<TypeStatutCompte> query = em.createQuery(
            "SELECT t FROM TypeStatutCompte t WHERE t.libelle = :libelle", 
            TypeStatutCompte.class
        );
        query.setParameter("libelle", libelle);
        List<TypeStatutCompte> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}