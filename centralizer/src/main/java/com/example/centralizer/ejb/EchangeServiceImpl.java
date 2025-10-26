package com.example.centralizer.ejb;

import com.example.centralizer.dto.echange.Echange;
import jakarta.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service EJB Stateless pour les opérations d'échange
 * Utilise le lookup JNDI pour accéder aux services EJB distants du module echange
 */
@Stateless
public class EchangeServiceImpl implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(EchangeServiceImpl.class.getName());
    
    private Object echangeServiceRemote;

    /**
     * Initialise le service EJB distant via JNDI
     */
    private void initializeRemoteService() {
        if (echangeServiceRemote == null) {
            try {
                InitialContext ctx = new InitialContext();
                // Lookup du service EJB distant du module echange
                echangeServiceRemote = ctx.lookup(
                    "ejb:eap/echange/EchangeServiceBean!com.example.echange.ejb.EchangeServiceRemote"
                );
                LOGGER.info("EchangeServiceRemote initialisé avec succès");
            } catch (NamingException e) {
                LOGGER.severe("Erreur lors du lookup du service EJB distant: " + e.getMessage());
                throw new RuntimeException("Impossible de localiser le service EJB distant", e);
            }
        }
    }
    
    /**
     * Récupère tous les taux de change
     */
    public List<Echange> getAllEchanges() {
        try {
            initializeRemoteService();
            
            @SuppressWarnings("unchecked")
            List<Object> result = (List<Object>) echangeServiceRemote.getClass()
                .getMethod("getAllEchanges")
                .invoke(echangeServiceRemote);
            
            LOGGER.info("Taux d'échange récupérés: " + (result != null ? result.size() : 0));
            return convertToEchangeDTOList(result);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des taux d'échange: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Récupère les taux de change actifs à une date donnée
     */
    public List<Echange> getEchangesActifs(LocalDate date) {
        try {
            initializeRemoteService();
            
            @SuppressWarnings("unchecked")
            List<Object> result = (List<Object>) echangeServiceRemote.getClass()
                .getMethod("getEchangesActifs", LocalDate.class)
                .invoke(echangeServiceRemote, date);
            
            LOGGER.info("Taux actifs à " + date + " récupérés: " + (result != null ? result.size() : 0));
            return convertToEchangeDTOList(result);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des taux à " + date + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Convertit un montant en devise étrangère vers Ariary à une date donnée
     */
    public BigDecimal convertirVersAriary(String devise, BigDecimal montant, LocalDate date) {
        try {
            initializeRemoteService();
            
            BigDecimal result = (BigDecimal) echangeServiceRemote.getClass()
                .getMethod("convertirVersAriary", String.class, BigDecimal.class, LocalDate.class)
                .invoke(echangeServiceRemote, devise, montant, date);
            
            LOGGER.info("Conversion à " + date + ": " + montant + " " + devise + " = " + result + " Ar");
            return result != null ? result : BigDecimal.ZERO;
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la conversion à date: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Convertit un montant en Ariary vers une devise étrangère à une date donnée
     */
    public BigDecimal convertirDepuisAriary(String devise, BigDecimal montantAriary, LocalDate date) {
        try {
            initializeRemoteService();
            
            BigDecimal result = (BigDecimal) echangeServiceRemote.getClass()
                .getMethod("convertirDepuisAriary", String.class, BigDecimal.class, LocalDate.class)
                .invoke(echangeServiceRemote, devise, montantAriary, date);
            
            LOGGER.info("Conversion à " + date + ": " + montantAriary + " Ar = " + result + " " + devise);
            return result != null ? result : BigDecimal.ZERO;
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la conversion: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Extrait le code devise depuis le nom complet (ex: "EUR/MGA" -> "EUR")
     */
    public String extraireCodeDevise(String nomComplet) {
        if (nomComplet != null && nomComplet.contains("/")) {
            return nomComplet.split("/")[0];
        }
        return nomComplet;
    }

    // ==================== Méthodes de conversion DTO ====================

    private Echange convertToEchangeDTO(Object remote) {
        if (remote == null) return null;
        try {
            Echange dto = new Echange();
            Object nomObj = remote.getClass().getMethod("getNom").invoke(remote);
            Object dateDebutObj = remote.getClass().getMethod("getDateDebut").invoke(remote);
            Object dateFinObj = remote.getClass().getMethod("getDateFin").invoke(remote);
            Object valeurObj = remote.getClass().getMethod("getValeur").invoke(remote);
            
            if (nomObj != null) dto.setNom(nomObj.toString());
            if (dateDebutObj != null) dto.setDateDebut((LocalDate) dateDebutObj);
            if (dateFinObj != null) dto.setDateFin((LocalDate) dateFinObj);
            if (valeurObj != null) dto.setValeur((BigDecimal) valeurObj);
            
            return dto;
        } catch (Exception e) {
            LOGGER.warning("Erreur lors de la conversion Echange: " + e.getMessage());
            return null;
        }
    }

    private List<Echange> convertToEchangeDTOList(List<Object> remoteList) {
        List<Echange> dtoList = new ArrayList<>();
        if (remoteList != null) {
            for (Object remote : remoteList) {
                Echange dto = convertToEchangeDTO(remote);
                if (dto != null) {
                    dtoList.add(dto);
                }
            }
        }
        return dtoList;
    }
}
