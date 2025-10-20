package com.example.serveurcomptecourant.repository;

import java.util.List;

import com.example.serveurcomptecourant.models.Utilisateur;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class UtilisateurRepository {
    @PersistenceContext(unitName = "ServeurCompteCourantPU")
    private EntityManager em;

    public Utilisateur save(Utilisateur utilisateur) {
        if (utilisateur.getIdUtilisateur() == null) {
            em.persist(utilisateur);
            return utilisateur;
        } else {
            return em.merge(utilisateur);
        }
    }

    public Utilisateur find(Integer idUtilisateur) {
        return em.find(Utilisateur.class, idUtilisateur);
    }

    public Utilisateur findByNomUtilisateur(String nomUtilisateur) {
        try {
            return em.createQuery("SELECT u FROM Utilisateur u WHERE u.nomUtilisateur = :nomUtilisateur", Utilisateur.class)
                    .setParameter("nomUtilisateur", nomUtilisateur)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }

    public List<Utilisateur> findAll() {
        return em.createQuery("SELECT u FROM Utilisateur u", Utilisateur.class).getResultList();
    }

    public List<Utilisateur> findByDirection(Integer idDirection) {
        return em.createQuery("SELECT u FROM Utilisateur u WHERE u.idDirection = :idDirection", Utilisateur.class)
                .setParameter("idDirection", idDirection)
                .getResultList();
    }

    public List<Utilisateur> findByRole(Integer role) {
        return em.createQuery("SELECT u FROM Utilisateur u WHERE u.roleUtilisateur = :role", Utilisateur.class)
                .setParameter("role", role)
                .getResultList();
    }

    public void delete(Utilisateur utilisateur) {
        em.remove(em.contains(utilisateur) ? utilisateur : em.merge(utilisateur));
    }
}