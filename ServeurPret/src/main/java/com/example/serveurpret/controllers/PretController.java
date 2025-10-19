package com.example.serveurpret.controllers;

import com.example.serveurpret.models.Pret;
import com.example.serveurpret.services.PretService;
import com.example.serveurpret.services.ParametresPretService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

@Path("/pret")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PretController {

    @EJB
    private PretService pretService;

    @EJB
    private ParametresPretService parametresService;

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
    public Response getByClientId(@PathParam("clientId") Integer clientId) {
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

    @POST
    @Path("/complet")
    public Response createPretComplet(java.util.Map<String, Object> requestBody) {
        try {
            // Extraction des paramètres
            Integer clientId = Integer.valueOf(requestBody.get("clientId").toString());
            java.math.BigDecimal montant = new java.math.BigDecimal(requestBody.get("montant").toString());
            java.math.BigDecimal revenu = new java.math.BigDecimal(requestBody.get("revenu").toString());
            Integer dureeMois = Integer.valueOf(requestBody.get("dureeMois").toString());
            Integer modaliteId = Integer.valueOf(requestBody.get("modaliteId").toString());
            Integer typeRemboursementId = Integer.valueOf(requestBody.get("typeRemboursementId").toString());

            // Validation du plafond selon le revenu
            if (!parametresService.isPretAutorise(revenu, montant)) {
                BigDecimal montantMax = parametresService.getMontantMaxAutorise(revenu);
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity("Montant demandé (" + montant + "€) dépasse le plafond autorisé pour ce revenu (" + montantMax + "€)")
                               .build();
            }

            // Création du prêt complet avec validation et amortissement
            Pret nouveauPret = pretService.createPret(clientId, montant, dureeMois, modaliteId, typeRemboursementId);
            
            return Response.status(Response.Status.CREATED).entity(nouveauPret).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Paramètres invalides: " + e.getMessage())
                           .build();
        } catch (Exception e) {
            return Response.serverError()
                           .entity("Erreur lors de la création du prêt complet : " + e.getMessage())
                           .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        try {
            Pret pret = pretService.getPretById(id);
            if (pret == null) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("Prêt non trouvé avec l'ID: " + id)
                               .build();
            }
            return Response.ok(pret).build();
        } catch (Exception e) {
            return Response.serverError()
                           .entity("Erreur lors de la récupération du prêt : " + e.getMessage())
                           .build();
        }
    }

    // ========== ENDPOINTS POUR LES PARAMETRES ==========

    @GET
    @Path("/parametres/modalites")
    public Response getAllModalites() {
        try {
            return Response.ok(parametresService.getAllModalites()).build();
        } catch (Exception e) {
            return Response.serverError()
                           .entity("Erreur lors de la récupération des modalités : " + e.getMessage())
                           .build();
        }
    }

    @GET
    @Path("/parametres/types-remboursement")
    public Response getAllTypesRemboursement() {
        try {
            return Response.ok(parametresService.getAllTypesRemboursement()).build();
        } catch (Exception e) {
            return Response.serverError()
                           .entity("Erreur lors de la récupération des types de remboursement : " + e.getMessage())
                           .build();
        }
    }

    @GET
    @Path("/parametres/plage-duree")
    public Response getPlageDureeByMontant(@QueryParam("montant") BigDecimal montant) {
        try {
            if (montant == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity("Le paramètre montant est requis")
                               .build();
            }
            return Response.ok(parametresService.getPlageDureeByMontant(montant)).build();
        } catch (Exception e) {
            return Response.serverError()
                           .entity("Erreur lors de la récupération de la plage de durée : " + e.getMessage())
                           .build();
        }
    }

    @GET
    @Path("/parametres/statut-en-cours")
    public Response getStatutEnCoursId() {
        try {
            // Recherche du statut "En cours" dans la base
            return Response.ok(1).build(); // Retourne l'ID du statut "En cours"
        } catch (Exception e) {
            return Response.serverError()
                           .entity("Erreur lors de la récupération du statut En cours : " + e.getMessage())
                           .build();
        }
    }

    @GET
    @Path("/parametres/methodes-remboursement")
    public Response getMethodesRemboursement() {
        try {
            return Response.ok(parametresService.getMethodesRemboursement()).build();
        } catch (Exception e) {
            return Response.serverError()
                           .entity("Erreur lors de la récupération des méthodes de remboursement : " + e.getMessage())
                           .build();
        }
    }

    @GET
    @Path("/parametres/validation-plafond")
    public Response validatePlafondPret(@QueryParam("revenu") BigDecimal revenu, 
                                       @QueryParam("montant") BigDecimal montant) {
        try {
            if (revenu == null || montant == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity("Les paramètres revenu et montant sont requis")
                               .build();
            }
            
            boolean autorise = parametresService.isPretAutorise(revenu, montant);
            BigDecimal montantMax = parametresService.getMontantMaxAutorise(revenu);
            
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("autorise", autorise);
            result.put("montantMax", montantMax);
            result.put("montantDemande", montant);
            result.put("revenu", revenu);
            
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.serverError()
                           .entity("Erreur lors de la validation du plafond : " + e.getMessage())
                           .build();
        }
    }

    @GET
    @Path("/{id}/prochain-remboursement")
    public Response getInfosProchainRemboursement(@PathParam("id") Long id) {
        try {
            return Response.ok(pretService.getInfosProchainRemboursement(id)).build();
        } catch (Exception e) {
            return Response.serverError()
                           .entity("Erreur lors de la récupération des informations de remboursement : " + e.getMessage())
                           .build();
        }
    }

    @GET
    @Path("/{id}/remboursements")
    public Response getHistoriqueRemboursements(@PathParam("id") Long id) {
        try {
            return Response.ok(pretService.getHistoriqueRemboursements(id)).build();
        } catch (Exception e) {
            return Response.serverError()
                           .entity("Erreur lors de la récupération de l'historique des remboursements : " + e.getMessage())
                           .build();
        }
    }

    @POST
    @Path("/{id}/payer")
    public Response effectuerRemboursement(@PathParam("id") Long id, java.util.Map<String, Object> paiementData) {
        try {
            String datePaiement = (String) paiementData.get("datePaiement");
            BigDecimal montant = new BigDecimal(paiementData.get("montant").toString());
            Integer idMethodeRemboursement = Integer.valueOf(paiementData.get("idMethodeRemboursement").toString());
            
            return Response.ok(pretService.effectuerRemboursement(id, datePaiement, montant, idMethodeRemboursement)).build();
        } catch (Exception e) {
            return Response.serverError()
                           .entity("Erreur lors du traitement du paiement : " + e.getMessage())
                           .build();
        }
    }
}
