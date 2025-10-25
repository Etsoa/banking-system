package com.example.echange.services;

import com.example.echange.models.Echange;
import jakarta.ejb.Stateful;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Stateful
public class EchangeService implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String JSON_FILE_NAME = "echanges.json";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    private List<Echange> echanges;
    
    public EchangeService() {
        this.echanges = new ArrayList<>();
        chargerEchanges();
    }
    
    /**
     * Charge les échanges depuis le fichier JSON
     */
    private void chargerEchanges() {
        try {
            // Charger le fichier depuis les resources
            java.io.InputStream is = getClass().getClassLoader().getResourceAsStream(JSON_FILE_NAME);
            if (is == null) {
                throw new RuntimeException("Fichier " + JSON_FILE_NAME + " introuvable dans les resources");
            }
            
            JsonReader reader = Json.createReader(is);
            JsonArray jsonArray = reader.readArray();
            echanges.clear();
            
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObj = jsonArray.getJsonObject(i);
                
                String nom = jsonObj.getString("nom");
                LocalDate dateDebut = LocalDate.parse(jsonObj.getString("dateDebut"), DATE_FORMATTER);
                LocalDate dateFin = jsonObj.isNull("dateFin") ? null : 
                    LocalDate.parse(jsonObj.getString("dateFin"), DATE_FORMATTER);
                BigDecimal valeur = new BigDecimal(jsonObj.getString("valeur"));
                
                echanges.add(new Echange(nom, dateDebut, dateFin, valeur));
            }
            
            reader.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du chargement du fichier JSON : " + e.getMessage());
        }
    }
    
    /**
     * Sauvegarde les échanges en mémoire (le fichier JSON source n'est pas modifié)
     */
    private void sauvegarderEchanges() {
        // Les modifications sont conservées en mémoire pendant la session
        // Le fichier JSON source reste inchangé
    }
    
    /**
     * Récupère tous les échanges
     */
    public List<Echange> getTousLesEchanges() {
        return new ArrayList<>(echanges);
    }
    
    /**
     * Récupère un échange par son nom
     */
    public Echange getEchangeParNom(String nom) {
        return echanges.stream()
            .filter(e -> e.getNom().equalsIgnoreCase(nom))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Récupère le taux de change actif pour une devise donnée
     */
    public Echange getEchangeActif(String nom) {
        return echanges.stream()
            .filter(e -> e.getNom().equalsIgnoreCase(nom) && e.estActif())
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Convertit un montant en devise étrangère vers Ariary
     */
    public BigDecimal convertirVersAriary(String nomDevise, BigDecimal montant) {
        Echange echange = getEchangeActif(nomDevise);
        if (echange == null) {
            throw new IllegalArgumentException("Aucun taux de change actif trouvé pour : " + nomDevise);
        }
        return montant.multiply(echange.getValeur());
    }
    
    /**
     * Convertit un montant en Ariary vers une devise étrangère
     */
    public BigDecimal convertirDepuisAriary(String nomDevise, BigDecimal montantAriary) {
        Echange echange = getEchangeActif(nomDevise);
        if (echange == null) {
            throw new IllegalArgumentException("Aucun taux de change actif trouvé pour : " + nomDevise);
        }
        return montantAriary.divide(echange.getValeur(), 2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * Ajoute un nouvel échange
     */
    public void ajouterEchange(Echange echange) {
        // Désactiver l'ancien taux si un nouveau est ajouté pour la même devise
        Echange ancien = getEchangeActif(echange.getNom());
        if (ancien != null && ancien.getDateFin() == null) {
            ancien.setDateFin(echange.getDateDebut().minusDays(1));
        }
        
        echanges.add(echange);
        sauvegarderEchanges();
    }
    
    /**
     * Met à jour un échange existant
     */
    public boolean mettreAJourEchange(String nom, Echange nouvelEchange) {
        Echange echange = getEchangeParNom(nom);
        if (echange == null) {
            return false;
        }
        
        echange.setDateDebut(nouvelEchange.getDateDebut());
        echange.setDateFin(nouvelEchange.getDateFin());
        echange.setValeur(nouvelEchange.getValeur());
        
        sauvegarderEchanges();
        return true;
    }
    
    /**
     * Supprime un échange
     */
    public boolean supprimerEchange(String nom) {
        boolean removed = echanges.removeIf(e -> e.getNom().equalsIgnoreCase(nom));
        if (removed) {
            sauvegarderEchanges();
        }
        return removed;
    }
    
    /**
     * Recharge les échanges depuis le fichier
     */
    public void recharger() {
        chargerEchanges();
    }
}
