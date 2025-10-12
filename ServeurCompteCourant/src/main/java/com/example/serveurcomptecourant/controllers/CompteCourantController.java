package com.example.serveurcomptecourant.controllers;

import com.example.serveurcomptecourant.models.CompteCourant;
import com.example.serveurcomptecourant.services.CompteCourantService;
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
    public List<CompteCourant> getAll() {
        return compteService.getAllComptes();
    }

    @GET
    @Path("/client/{clientId}")
    public List<CompteCourant> getByClientId(@PathParam("clientId") Long clientId) {
        return compteService.getComptesByClientId(clientId);
    }

    @GET
    @Path("/{id}")
    public CompteCourant getById(@PathParam("id") Long id) {
        return compteService.getCompteById(id);
    }

    @POST
    public void create(CompteCourant compte) {
        compteService.createCompte(compte);
    }
}
