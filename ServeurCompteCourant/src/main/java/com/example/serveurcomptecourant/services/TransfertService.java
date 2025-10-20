package com.example.serveurcomptecourant.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.serveurcomptecourant.exceptions.CompteCourantBusinessException;
import com.example.serveurcomptecourant.exceptions.CompteCourantException;
import com.example.serveurcomptecourant.models.CompteCourant;
import com.example.serveurcomptecourant.models.Transfert;
import com.example.serveurcomptecourant.models.TransfertAvecFrais;
import com.example.serveurcomptecourant.models.Transaction;
import com.example.serveurcomptecourant.models.Frais;
import com.example.serveurcomptecourant.repository.TransfertRepository;
import com.example.serveurcomptecourant.repository.CompteCourantRepository;
import com.example.serveurcomptecourant.repository.TransactionRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;

@Stateless
public class TransfertService {
    private static final Logger LOGGER = Logger.getLogger(TransfertService.class.getName());
    
    @EJB
    private TransfertRepository transfertRepository;
    
    @EJB
    private CompteCourantRepository compteRepository;
    
    @EJB
    private TransactionRepository transactionRepository;
    
    @EJB
    private FraisService fraisService;

    /**
     * Récupère tous les transferts
     */
    public List<Transfert> getAllTransferts() throws CompteCourantException {
        try {
            return transfertRepository.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transferts", e);
            throw new CompteCourantException("Erreur lors de la récupération des transferts", e);
        }
    }

    /**
     * Récupère les transferts d'un compte avec les frais associés
     */
    public List<TransfertAvecFrais> getTransfertsByCompteAvecFrais(String compteId) throws CompteCourantException {
        try {
            List<Transfert> transferts = getTransfertsByCompte(compteId);
            return enrichirTransfertsAvecFrais(transferts);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transferts avec frais du compte " + compteId, e);
            throw new CompteCourantException("Erreur lors de la récupération des transferts avec frais", e);
        }
    }
    
    /**
     * Récupère tous les transferts avec les frais associés
     */
    public List<TransfertAvecFrais> getAllTransfertsAvecFrais() throws CompteCourantException {
        try {
            List<Transfert> transferts = getAllTransferts();
            return enrichirTransfertsAvecFrais(transferts);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de tous les transferts avec frais", e);
            throw new CompteCourantException("Erreur lors de la récupération des transferts avec frais", e);
        }
    }
    
    /**
     * Enrichit une liste de transferts avec les informations sur les frais
     */
    private List<TransfertAvecFrais> enrichirTransfertsAvecFrais(List<Transfert> transferts) throws CompteCourantException {
        List<TransfertAvecFrais> transfertsAvecFrais = new java.util.ArrayList<>();
        
        for (Transfert transfert : transferts) {
            TransfertAvecFrais transfertAvecFrais = new TransfertAvecFrais(transfert);
            
            try {
                // Récupérer la transaction de sortie (virement sortant)
                if (transfert.getIdTransactionEnvoyeur() != null) {
                    Long idTransactionEnvoyeur = Long.parseLong(transfert.getIdTransactionEnvoyeur());
                    Transaction transactionEnvoyeur = transactionRepository.findById(idTransactionEnvoyeur.intValue());
                    
                    if (transactionEnvoyeur != null) {
                        // Récupérer les frais applicables pour cette transaction de type "Virement sortant"
                        Frais frais = fraisService.findCurrentFrais(
                            "Virement sortant",
                            transfert.getMontant(),
                            transactionEnvoyeur.getDateTransaction()
                        );
                        
                        if (frais != null) {
                            // Calculer le montant des frais
                            BigDecimal montantFrais = BigDecimal.valueOf(frais.getValeur());
                            
                            transfertAvecFrais.setFraisEnvoyeur(montantFrais);
                            transfertAvecFrais.setLibelleFraisEnvoyeur(frais.getNom());
                            transfertAvecFrais.setMontantTotalEnvoyeur(transfert.getMontant().add(montantFrais));
                        } else {
                            // Pas de frais applicables
                            transfertAvecFrais.setFraisEnvoyeur(BigDecimal.ZERO);
                            transfertAvecFrais.setLibelleFraisEnvoyeur("Aucun frais");
                            transfertAvecFrais.setMontantTotalEnvoyeur(transfert.getMontant());
                        }
                    }
                }
                
                // Si pas de transaction trouvée, initialiser avec des valeurs par défaut
                if (transfertAvecFrais.getFraisEnvoyeur() == null) {
                    transfertAvecFrais.setFraisEnvoyeur(BigDecimal.ZERO);
                    transfertAvecFrais.setLibelleFraisEnvoyeur("Non calculé");
                    transfertAvecFrais.setMontantTotalEnvoyeur(transfert.getMontant());
                }
                
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Erreur lors du calcul des frais pour le transfert " + transfert.getId(), e);
                // En cas d'erreur, initialiser avec des valeurs par défaut
                transfertAvecFrais.setFraisEnvoyeur(BigDecimal.ZERO);
                transfertAvecFrais.setLibelleFraisEnvoyeur("Erreur calcul");
                transfertAvecFrais.setMontantTotalEnvoyeur(transfert.getMontant());
            }
            
            transfertsAvecFrais.add(transfertAvecFrais);
        }
        
        return transfertsAvecFrais;
    }

    /**
     * Récupère les transferts d'un compte (envoyeur ou receveur)
     */
    public List<Transfert> getTransfertsByCompte(String compteId) throws CompteCourantException {
        try {
            if (compteId == null || compteId.trim().isEmpty()) {
                throw new CompteCourantBusinessException("L'ID du compte est obligatoire");
            }
            
            // Vérifier que le compte existe
            CompteCourant compte = compteRepository.find(compteId);
            if (compte == null) {
                throw new CompteCourantBusinessException.CompteNotFoundException(0L);
            }
            
            return transfertRepository.findByCompte(compteId);
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des transferts du compte " + compteId, e);
            throw new CompteCourantException("Erreur lors de la récupération des transferts", e);
        }
    }
    /**
     * Crée un transfert (appelé par TransactionService)
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Transfert createTransfert(String compteEnvoyeur, String compteReceveur, BigDecimal montant, 
                                   Transaction transactionSortante, Transaction transactionEntrante) throws CompteCourantException {
        try {
            // Validation des paramètres
            validateTransfertData(compteEnvoyeur, compteReceveur, montant, transactionSortante, transactionEntrante);
            
            // Créer le transfert
            Transfert transfert = new Transfert();
            transfert.setMontant(montant);
            transfert.setEnvoyer(compteEnvoyeur);
            transfert.setReceveur(compteReceveur);
            transfert.setIdTransactionEnvoyeur(transactionSortante.getId().toString());
            transfert.setIdTransactionReceveur(transactionEntrante.getId().toString());
            transfert.setDateTransfert(transactionSortante.getDateTransaction().toLocalDate());
            
            return transfertRepository.save(transfert);
            
        } catch (CompteCourantBusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création du transfert", e);
            throw new CompteCourantException("Erreur lors de la création du transfert", e);
        }
    }
    /**
     * Validation des données de transfert
     */
    private void validateTransfertData(String compteEnvoyeur, String compteReceveur, BigDecimal montant,
                                     Transaction transactionSortante, Transaction transactionEntrante) throws CompteCourantBusinessException {
        if (compteEnvoyeur == null || compteEnvoyeur.trim().isEmpty()) {
            throw new CompteCourantBusinessException("Le compte envoyeur est obligatoire");
        }
        
        if (compteReceveur == null || compteReceveur.trim().isEmpty()) {
            throw new CompteCourantBusinessException("Le compte receveur est obligatoire");
        }
        
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CompteCourantBusinessException.MontantInvalideException(
                montant != null ? montant.doubleValue() : 0.0
            );
        }
        
        if (compteEnvoyeur.equals(compteReceveur)) {
            throw new CompteCourantBusinessException("Le compte envoyeur et receveur doivent être différents");
        }
        
        if (transactionSortante == null || transactionEntrante == null) {
            throw new CompteCourantBusinessException("Les transactions associées sont obligatoires");
        }
        
        if (transactionSortante.getId() == null || transactionEntrante.getId() == null) {
            throw new CompteCourantBusinessException("Les transactions doivent être sauvegardées avant de créer le transfert");
        }
    }
}