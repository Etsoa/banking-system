package com.example.centralizer.services;

import com.example.centralizer.models.HistoriqueRevenus;
import com.example.centralizer.repositories.HistoriqueRevenusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class HistoriqueRevenuService {
    private static final Logger LOGGER = Logger.getLogger(HistoriqueRevenuService.class.getName());

    @Autowired
    private HistoriqueRevenusRepository historiqueRevenusRepository;

    /**
     * Récupère le revenu actuel d'un client à une date donnée
     */
    public BigDecimal getCurrentRevenuByClient(Integer idClient, LocalDate dateReference) {
        try {
            Optional<HistoriqueRevenus> historiqueRevenu = historiqueRevenusRepository
                .findCurrentRevenuByClientAndDate(idClient, dateReference);
            
            if (historiqueRevenu.isPresent()) {
                return historiqueRevenu.get().getMontantRevenus();
            } else {
                // Si pas de revenu pour la date donnée, essayer de récupérer le plus récent
                return getLatestRevenuByClient(idClient);
            }
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération du revenu actuel du client " + idClient + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Récupère le revenu le plus récent d'un client
     */
    public BigDecimal getLatestRevenuByClient(Integer idClient) {
        try {
            Optional<HistoriqueRevenus> historiqueRevenu = historiqueRevenusRepository
                .findLatestRevenuByClient(idClient);
            
            if (historiqueRevenu.isPresent()) {
                return historiqueRevenu.get().getMontantRevenus();
            } else {
                LOGGER.warning("Aucun historique de revenu trouvé pour le client " + idClient);
                return null;
            }
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération du revenu le plus récent du client " + idClient + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Récupère le revenu actuel d'un client (utilise la date du jour)
     */
    public BigDecimal getCurrentRevenuByClient(Integer idClient) {
        return getCurrentRevenuByClient(idClient, LocalDate.now());
    }
}