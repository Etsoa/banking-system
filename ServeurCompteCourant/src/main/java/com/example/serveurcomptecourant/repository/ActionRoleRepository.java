package com.example.serveurcomptecourant.repository;

import java.util.List;

import com.example.serveurcomptecourant.models.ActionRole;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class ActionRoleRepository {
    @PersistenceContext(unitName = "ServeurCompteCourantPU")
    private EntityManager em;

    public ActionRole save(ActionRole actionRole) {
        if (actionRole.getIdActionRole() == null) {
            em.persist(actionRole);
            return actionRole;
        } else {
            return em.merge(actionRole);
        }
    }

    public ActionRole find(Integer idActionRole) {
        return em.find(ActionRole.class, idActionRole);
    }

    public List<ActionRole> findAll() {
        return em.createQuery("SELECT ar FROM ActionRole ar", ActionRole.class).getResultList();
    }

    public List<ActionRole> findByTable(String nomTable) {
        return em.createQuery("SELECT ar FROM ActionRole ar WHERE ar.nomTable = :nomTable", ActionRole.class)
                .setParameter("nomTable", nomTable)
                .getResultList();
    }

    public ActionRole findByTableAndAction(String nomTable, String nomAction) {
        try {
            return em.createQuery("SELECT ar FROM ActionRole ar WHERE ar.nomTable = :nomTable AND ar.nomAction = :nomAction", ActionRole.class)
                    .setParameter("nomTable", nomTable)
                    .setParameter("nomAction", nomAction)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }

    public Integer getRoleMinimumForAction(String nomTable, String nomAction) {
        try {
            return em.createQuery("SELECT ar.roleMinimum FROM ActionRole ar WHERE ar.nomTable = :nomTable AND ar.nomAction = :nomAction", Integer.class)
                    .setParameter("nomTable", nomTable)
                    .setParameter("nomAction", nomAction)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }

    public void delete(ActionRole actionRole) {
        em.remove(em.contains(actionRole) ? actionRole : em.merge(actionRole));
    }
}