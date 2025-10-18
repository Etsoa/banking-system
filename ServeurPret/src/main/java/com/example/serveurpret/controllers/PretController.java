package com.example.serveurpret.controllers;

import com.example.serveurpret.models.Pret;
import com.example.serveurpret.services.PretService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/pret")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PretController {

    @EJB
    private PretService pretService;

    @GET
    public Response getAll() {
        try {
            List<Pret> prets = pretService.getAllPrets();
            return Response.ok(prets).build();
        } catch (Exception e) {
            return Response.serverError()
                           .entity("Erreur lors de la récupération des prêts : " + e.getMessage())
                           .build();
        }
    }

    @GET
    @Path("/client/{clientId}")
    public Response getByClientId(@PathParam("clientId") String clientId) {
        try {
            List<Pret> prets = pretService.getPretsByClientId(clientId);
            return Response.ok(prets).build();
        } catch (Exception e) {
            return Response.serverError()
                           .entity("Erreur lors de la récupération des prêts pour le client " + clientId + " : " + e.getMessage())
                           .build();
        }
    }

    @POST
    public Response create(Pret pret) {
        try {
            pretService.createPret(pret);
            return Response.status(Response.Status.CREATED).entity(pret).build();
        } catch (Exception e) {
            return Response.serverError()
                           .entity("Erreur lors de la création du prêt : " + e.getMessage())
                           .build();
        }
    }
}
