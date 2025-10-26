package com.example.echange.ejb;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.echange.exceptions.EchangeException;
import com.example.echange.models.Echange;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateful;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

/**
 * Bean stateful qui garde en mémoire la liste des echanges.
 * Implémente à la fois l'interface locale et distante.
 */
@Stateful
public class EchangeServiceBean implements EchangeServiceRemote {

    private final List<Echange> echanges = new ArrayList<>();

    @PostConstruct
    private void init() {
        chargerEchanges();
    }


    @Override
    public List<Echange> getAllEchanges() {
        // renvoyer une copie immuable pour sécurité
        return Collections.unmodifiableList(new ArrayList<>(echanges));
    }

    /**
     * Retourne la liste des échanges actifs à la date donnée.
     */
    public List<Echange> getActifFromDate(LocalDate date) {
        List<Echange> actifs = new ArrayList<>();
        for (Echange e : echanges) {
            if (e.estActif(date)) {
                actifs.add(e);
            }
        }
        return actifs;
    }

    /**
     * Récupère les taux de change actifs à une date donnée
     */
    @Override
    public List<Echange> getEchangesActifs(LocalDate date) {
        List<Echange> actifs = new ArrayList<>();
        for (Echange e : echanges) {
            if (e.estActif(date)) {
                actifs.add(e);
            }
        }
        return actifs;
    }

    /**
     * Convertit un montant en devise étrangère vers Ariary à une date donnée
     */
    @Override
    public BigDecimal convertirVersAriary(String devise, BigDecimal montant, LocalDate date) {
        // Chercher le taux d'échange pour cette devise à la date donnée
        for (Echange e : echanges) {
            if (e.getNom() != null && e.getNom().contains(devise) && e.estActif(date)) {
                // Format attendu: "EUR/MGA" -> on cherche "EUR"
                if (e.getNom().startsWith(devise + "/")) {
                    return montant.multiply(e.getValeur());
                }
            }
        }
        throw new EchangeException("Taux de change non trouvé pour " + devise + " à la date " + date);
    }

    /**
     * Convertit un montant en Ariary vers une devise étrangère à une date donnée
     */
    @Override
    public BigDecimal convertirDepuisAriary(String devise, BigDecimal montantAriary, LocalDate date) {
        for (Echange e : echanges) {
            if (e.getNom() != null && e.getNom().contains(devise) && e.estActif(date)) {
                // Format attendu: "EUR/MGA" -> on cherche "EUR"
                if (e.getNom().startsWith(devise + "/")) {
                    return montantAriary.divide(e.getValeur(), 2, RoundingMode.HALF_UP);
                }
            }
        }
        throw new EchangeException("Taux de change non trouvé pour " + devise + " à la date " + date);
    }

    @Override
    public void addEchange(Echange e) {
        if (e == null) {
            throw new EchangeException("Echange null");
        }
        echanges.add(e);
        // Optionnel : persister sur disque si nécessaire (non implémenté ici)
    }

    @Override
    public boolean removeEchangeByNom(String nom) {
        boolean removed = echanges.removeIf(x -> x.getNom() != null && x.getNom().equalsIgnoreCase(nom));
        return removed;
    }

    @Override
    public void recharger() {
        chargerEchanges();
    }

    private void chargerEchanges() {
        echanges.clear();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("echanges.json")) {
            if (is == null) {
                // Aucun fichier : on laisse vide
                return;
            }
            try (JsonReader jr = Json.createReader(new InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8))) {
                JsonArray arr = jr.readArray();
                for (int i = 0; i < arr.size(); i++) {
                    JsonObject o = arr.getJsonObject(i);
                    Echange e = new Echange();
                    e.setNom(o.getString("nom", null));
                    String db = o.getString("dateDebut", null);
                    if (db != null && !db.equals("null")) {
                        e.setDateDebut(LocalDate.parse(db));
                    }
                    if (o.isNull("dateFin")) {
                        e.setDateFin(null);
                    } else {
                        String df = o.getString("dateFin", null);
                        if (df != null && !df.equals("null")) {
                            e.setDateFin(LocalDate.parse(df));
                        }
                    }
                    String val = o.getString("valeur", null);
                    if (val != null) {
                        e.setValeur(new BigDecimal(val));
                    }
                    echanges.add(e);
                }
            }
        } catch (Exception ex) {
            throw new EchangeException("Erreur chargement echanges.json: " + ex.getMessage(), ex);
        }
    }
}
