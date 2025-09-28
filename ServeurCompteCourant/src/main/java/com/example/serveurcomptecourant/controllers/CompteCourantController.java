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
        // Implement logic to get all CompteCourant
        return null;
    }

    @POST
    public void create(CompteCourant compte) {
        // Implement logic to create CompteCourant
    }
}
