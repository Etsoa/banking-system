package com.example.serveurcomptecourant.controllers;

import com.example.serveurcomptecourant.models.CompteCourant;
import com.example.serveurcomptecourant.models.CompteCourantAvecStatut;
import com.example.serveurcomptecourant.services.CompteCourantService;
import com.example.serveurcomptecourant.exceptions.CompteCourantException;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/compte-courant")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompteCourantController {
    
    @EJB
    private CompteCourantService compteService;

    @GET
    public List<CompteCourant> getAll() throws CompteCourantException {
        return compteService.getAllComptes();
    }

    @GET
    @Path("/avec-statut")
    public List<CompteCourantAvecStatut> getAllAvecStatut() throws CompteCourantException {
        return compteService.getAllComptesAvecStatut();
    }

    @GET
    @Path("/client/{clientId}")
    public List<CompteCourant> getByClientId(@PathParam("clientId") int clientId) throws CompteCourantException {
        return compteService.getComptesByClientId(clientId);
    }

    @GET
    @Path("/{id}")
    public CompteCourant getById(@PathParam("id") int id) throws CompteCourantException {
        return compteService.getCompteById(id);
    }

    @POST
    public CompteCourant create(CompteCourant compte) throws CompteCourantException {
        return compteService.createCompte(compte);
    }
}
