package com.example.echange.controllers;

import com.example.echange.models.Echange;
import com.example.echange.services.EchangeService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.List;

@Path("/echanges")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EchangeController {
    
    @EJB
    private EchangeService echangeService;
    
    /**
     * Récupère tous les taux de change
     * GET /echanges
     */
    @GET
    public Response getTousLesEchanges() {
        List<Echange> echanges = echangeService.getTousLesEchanges();
        return Response.ok(echanges).build();
    }
    
    /**
     * Récupère tous les taux de change actifs
     * GET /echanges/actifs
     */
    @GET
    @Path("/actifs")
    public Response getEchangesActifs() {
        List<Echange> echangesActifs = echangeService.getTousLesEchanges().stream()
            .filter(e -> e.estActif())
            .collect(java.util.stream.Collectors.toList());
        return Response.ok(echangesActifs).build();
    }
    
    /**
     * Récupère tous les taux de change actifs à une date donnée
     * GET /echanges/actifs/{date}
     */
    @GET
    @Path("/actifs/{date}")
    public Response getEchangesActifsADate(@PathParam("date") String dateStr) {
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
            List<Echange> echangesActifs = echangeService.getTousLesEchanges().stream()
                .filter(e -> e.estActif(date))
                .collect(java.util.stream.Collectors.toList());
            return Response.ok(echangesActifs).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"message\":\"Format de date invalide. Utilisez YYYY-MM-DD\"}")
                .build();
        }
    }
    
    /**
     * Récupère un taux de change par nom
     * GET /echanges/{nom}
     */
    @GET
    @Path("/{nom}")
    public Response getEchangeParNom(@PathParam("nom") String nom) {
        Echange echange = echangeService.getEchangeParNom(nom);
        if (echange == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"message\":\"Taux de change non trouvé\"}")
                .build();
        }
        return Response.ok(echange).build();
    }
    
    /**
     * Récupère le taux de change actif pour une devise
     * GET /echanges/{nom}/actif
     */
    @GET
    @Path("/{nom}/actif")
    public Response getEchangeActif(@PathParam("nom") String nom) {
        Echange echange = echangeService.getEchangeActif(nom);
        if (echange == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"message\":\"Aucun taux de change actif pour cette devise\"}")
                .build();
        }
        return Response.ok(echange).build();
    }
    
    /**
     * Convertit un montant en devise étrangère vers Ariary
     * GET /echanges/convertir/vers-ariary?devise={devise}&montant={montant}&date={date}
     */
    @GET
    @Path("/convertir/vers-ariary")
    public Response convertirVersAriary(
            @QueryParam("devise") String devise,
            @QueryParam("montant") BigDecimal montant,
            @QueryParam("date") String dateStr) {
        
        if (devise == null || montant == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"message\":\"Paramètres manquants : devise et montant requis\"}")
                .build();
        }
        
        try {
            BigDecimal resultat;
            if (dateStr != null && !dateStr.isEmpty()) {
                // Convertir avec taux actif à une date spécifique
                java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
                Echange echange = echangeService.getTousLesEchanges().stream()
                    .filter(e -> e.getNom().equalsIgnoreCase(devise) && e.estActif(date))
                    .findFirst()
                    .orElse(null);
                
                if (echange == null) {
                    return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\":\"Aucun taux de change actif pour " + devise + " à la date " + dateStr + "\"}")
                        .build();
                }
                resultat = montant.multiply(echange.getValeur());
            } else {
                // Convertir avec taux actif actuel
                resultat = echangeService.convertirVersAriary(devise, montant);
            }
            
            return Response.ok()
                .entity("{\"devise\":\"" + devise + "\",\"montant\":" + montant + 
                       ",\"montantAriary\":" + resultat + "}")
                .build();
        } catch (java.time.format.DateTimeParseException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"message\":\"Format de date invalide. Utilisez YYYY-MM-DD\"}")
                .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"message\":\"" + e.getMessage() + "\"}")
                .build();
        }
    }
    
    /**
     * Convertit un montant en Ariary vers une devise étrangère
     * GET /echanges/convertir/depuis-ariary?devise={devise}&montant={montant}
     */
    @GET
    @Path("/convertir/depuis-ariary")
    public Response convertirDepuisAriary(
            @QueryParam("devise") String devise,
            @QueryParam("montant") BigDecimal montant) {
        
        if (devise == null || montant == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"message\":\"Paramètres manquants : devise et montant requis\"}")
                .build();
        }
        
        try {
            BigDecimal resultat = echangeService.convertirDepuisAriary(devise, montant);
            return Response.ok()
                .entity("{\"devise\":\"" + devise + "\",\"montantAriary\":" + montant + 
                       ",\"montantDevise\":" + resultat + "}")
                .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"message\":\"" + e.getMessage() + "\"}")
                .build();
        }
    }
    
    /**
     * Ajoute un nouveau taux de change
     * POST /echanges
     */
    @POST
    public Response ajouterEchange(Echange echange) {
        if (echange == null || echange.getNom() == null || 
            echange.getDateDebut() == null || echange.getValeur() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"message\":\"Données invalides\"}")
                .build();
        }
        
        echangeService.ajouterEchange(echange);
        return Response.status(Response.Status.CREATED)
            .entity(echange)
            .build();
    }
    
    /**
     * Met à jour un taux de change
     * PUT /echanges/{nom}
     */
    @PUT
    @Path("/{nom}")
    public Response mettreAJourEchange(@PathParam("nom") String nom, Echange echange) {
        if (echange == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"message\":\"Données invalides\"}")
                .build();
        }
        
        boolean updated = echangeService.mettreAJourEchange(nom, echange);
        if (!updated) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"message\":\"Taux de change non trouvé\"}")
                .build();
        }
        
        return Response.ok(echange).build();
    }
    
    /**
     * Supprime un taux de change
     * DELETE /echanges/{nom}
     */
    @DELETE
    @Path("/{nom}")
    public Response supprimerEchange(@PathParam("nom") String nom) {
        boolean deleted = echangeService.supprimerEchange(nom);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"message\":\"Taux de change non trouvé\"}")
                .build();
        }
        
        return Response.ok()
            .entity("{\"message\":\"Taux de change supprimé avec succès\"}")
            .build();
    }
    
    /**
     * Recharge les taux de change depuis le fichier JSON
     * POST /echanges/recharger
     */
    @POST
    @Path("/recharger")
    public Response recharger() {
        echangeService.recharger();
        return Response.ok()
            .entity("{\"message\":\"Taux de change rechargés avec succès\"}")
            .build();
    }
}
