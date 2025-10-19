package com.example.centralizer.services;

import com.example.centralizer.models.compteCourantDTO.CompteCourant;
import com.example.centralizer.models.compteDepotDTO.Compte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Service pour gérer les transferts entre comptes dépôt et comptes courants
 * Implémente la règle métier : transfert normal + 2 transactions par serveur + 1 transfert par serveur
 */
@Service
public class TransfertInterSystemeService {
    private static final Logger LOGGER = Logger.getLogger(TransfertInterSystemeService.class.getName());

    @Autowired
    private CompteCourantService compteCourantService;

    @Autowired
    private CompteDepotService compteDepotService;

    @Autowired
    private TransactionCompteCourantService transactionCompteCourantService;

    @Autowired
    private TransactionCompteDepotService transactionCompteDepotService;

    @Autowired
    private HistoriqueRevenuService historiqueRevenuService;

    // Types de transaction
    private static final int TYPE_TRANSFERT_ENTRANT = 3; // Virement entrant pour le receveur
    private static final int TYPE_TRANSFERT_SORTANT = 4; // Virement sortant pour l'envoyeur

    /**
     * Effectue un transfert entre un compte dépôt et un compte courant
     * Règle métier :
     * 1. Vérification des comptes et des soldes
     * 2. Création de 2 transactions pour chaque serveur (sortante pour envoyeur, entrante pour receveur)
     * 3. Création d'1 transfert pour chaque serveur avec les infos nécessaires
     */
    @Transactional
    public boolean effectuerTransfertInterSysteme(
            String typeCompteEnvoyeur, String compteEnvoyeur,
            String typeCompteReceveur, String compteReceveur,
            BigDecimal montant, LocalDateTime dateTransfert) {
        
        LOGGER.info(String.format("Début transfert inter-système: %s(%s) -> %s(%s), montant: %s",
                compteEnvoyeur, typeCompteEnvoyeur, compteReceveur, typeCompteReceveur, montant));

        try {
            // 1. Validation et vérification des comptes
            if (!validerComptes(typeCompteEnvoyeur, compteEnvoyeur, typeCompteReceveur, compteReceveur)) {
                return false;
            }

            // 2. Vérification du solde du compte envoyeur
            if (!verifierSoldeEnvoyeur(typeCompteEnvoyeur, compteEnvoyeur, montant)) {
                throw new IllegalArgumentException("Solde insuffisant pour effectuer le transfert");
            }

            // 3. Effectuer les opérations selon le sens du transfert
            if ("compteDepot".equals(typeCompteEnvoyeur) && "compteCourant".equals(typeCompteReceveur)) {
                return effectuerTransfertDepotVersCourant(compteEnvoyeur, compteReceveur, montant, dateTransfert);
            } else if ("compteCourant".equals(typeCompteEnvoyeur) && "compteDepot".equals(typeCompteReceveur)) {
                return effectuerTransfertCourantVersDepot(compteEnvoyeur, compteReceveur, montant, dateTransfert);
            } else {
                throw new IllegalArgumentException("Type de transfert inter-système non supporté");
            }

        } catch (Exception e) {
            LOGGER.severe("Erreur lors du transfert inter-système: " + e.getMessage());
            throw new RuntimeException("Erreur lors du transfert inter-système: " + e.getMessage(), e);
        }
    }

    /**
     * Transfert de compte dépôt vers compte courant
     */
    private boolean effectuerTransfertDepotVersCourant(String compteDepotId, String compteCourantId, 
                                                      BigDecimal montant, LocalDateTime dateTransfert) {
        LOGGER.info("Effectuation transfert Dépôt -> Courant");

        try {
            // Récupérer les informations des comptes
            Compte compteDepot = compteDepotService.getCompteById(compteDepotId);
            CompteCourant compteCourant = compteCourantService.getCompteById(compteCourantId);

            if (compteDepot == null || compteCourant == null) {
                throw new IllegalArgumentException("Un des comptes est introuvable");
            }

            // 1. Créer transaction de sortie sur le serveur CompteDepot
            com.example.centralizer.models.compteDepotDTO.Transaction transactionSortieDepot = new com.example.centralizer.models.compteDepotDTO.Transaction();
            transactionSortieDepot.setIdCompte(compteDepotId);
            transactionSortieDepot.setIdTypeTransaction(TYPE_TRANSFERT_SORTANT);
            transactionSortieDepot.setMontant(montant); // Montant positif - le type détermine si c'est débit/crédit
            transactionSortieDepot.setDateTransaction(dateTransfert);

            com.example.centralizer.models.compteDepotDTO.Transaction resultSortieDepot = transactionCompteDepotService.createTransaction(transactionSortieDepot);
            if (resultSortieDepot == null) {
                throw new RuntimeException("Échec de la transaction de sortie sur le compte dépôt");
            }

            // 2. Créer transaction d'entrée sur le serveur CompteCourant
            com.example.centralizer.models.compteCourantDTO.Transaction transactionEntreeCourant = new com.example.centralizer.models.compteCourantDTO.Transaction();
            transactionEntreeCourant.setIdCompte(compteCourantId);
            transactionEntreeCourant.setIdTypeTransaction(TYPE_TRANSFERT_ENTRANT);
            transactionEntreeCourant.setMontant(montant); // Montant positif - le type détermine si c'est débit/crédit
            transactionEntreeCourant.setDateTransaction(dateTransfert);

            // Récupérer le revenu du client pour la validation du découvert
            BigDecimal revenu = null;
            try {
                revenu = historiqueRevenuService.getCurrentRevenuByClient(compteCourant.getIdClient());
            } catch (Exception e) {
                LOGGER.warning("Impossible de récupérer le revenu du client " + compteCourant.getIdClient());
            }

            com.example.centralizer.models.compteCourantDTO.Transaction resultEntreeCourant = transactionCompteCourantService.createTransaction(transactionEntreeCourant, compteCourant.getIdClient());
            if (resultEntreeCourant == null) {
                throw new RuntimeException("Échec de la transaction d'entrée sur le compte courant");
            }

            // 3. Créer transfert manuellement sur le serveur CompteDepot (avec les transactions créées)
            com.example.centralizer.models.compteDepotDTO.Transfert transfertDepot = new com.example.centralizer.models.compteDepotDTO.Transfert();
            // Ne pas définir idTransfert - auto-increment
            transfertDepot.setEnvoyer(compteDepotId);
            transfertDepot.setReceveur(compteCourantId); // Référence vers le compte courant
            transfertDepot.setMontant(montant);
            transfertDepot.setDateTransfert(dateTransfert.toLocalDate()); // Conversion LocalDateTime -> LocalDate
            transfertDepot.setIdTransactionEnvoyeur(resultSortieDepot.getIdTransaction().toString());
            transfertDepot.setIdTransactionReceveur("INTER_" + resultEntreeCourant.getIdTransaction().toString()); // Référence externe

            LOGGER.info("Création transfert depot: " + transfertDepot.getDateTransfert() + ", montant: " + transfertDepot.getMontant());
            com.example.centralizer.models.compteDepotDTO.Transfert resultTransfertDepot = transactionCompteDepotService.createTransfertInterSysteme(transfertDepot);

            // 4. Créer transfert manuellement sur le serveur CompteCourant (avec les transactions créées)
            com.example.centralizer.models.compteCourantDTO.Transfert transfertCourant = new com.example.centralizer.models.compteCourantDTO.Transfert();
            // Ne pas définir idTransfert - auto-increment
            transfertCourant.setEnvoyer(compteDepotId); // Référence vers le compte dépôt
            transfertCourant.setReceveur(compteCourantId);
            transfertCourant.setMontant(montant);
            transfertCourant.setDateTransfert(dateTransfert.toLocalDate());
            transfertCourant.setIdTransactionEnvoyeur("INTER_" + resultSortieDepot.getIdTransaction().toString()); // Référence externe
            transfertCourant.setIdTransactionReceveur(resultEntreeCourant.getIdTransaction().toString());

            com.example.centralizer.models.compteCourantDTO.Transfert resultTransfertCourant = transactionCompteCourantService.createTransfertInterSysteme(transfertCourant);

            boolean success = resultTransfertDepot != null && resultTransfertCourant != null;
            
            LOGGER.info("Transfert Dépôt -> Courant " + (success ? "réussi" : "échoué"));
            return success;

        } catch (Exception e) {
            LOGGER.severe("Erreur lors du transfert Dépôt -> Courant: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Transfert de compte courant vers compte dépôt
     */
    private boolean effectuerTransfertCourantVersDepot(String compteCourantId, String compteDepotId, 
                                                      BigDecimal montant, LocalDateTime dateTransfert) {
        LOGGER.info("Effectuation transfert Courant -> Dépôt");

        try {
            // Récupérer les informations des comptes
            CompteCourant compteCourant = compteCourantService.getCompteById(compteCourantId);
            Compte compteDepot = compteDepotService.getCompteById(compteDepotId);

            if (compteCourant == null || compteDepot == null) {
                throw new IllegalArgumentException("Un des comptes est introuvable");
            }

            // 1. Créer transaction de sortie sur le serveur CompteCourant
            com.example.centralizer.models.compteCourantDTO.Transaction transactionSortieCourant = new com.example.centralizer.models.compteCourantDTO.Transaction();
            transactionSortieCourant.setIdCompte(compteCourantId);
            transactionSortieCourant.setIdTypeTransaction(TYPE_TRANSFERT_SORTANT);
            transactionSortieCourant.setMontant(montant); // Montant positif - le type détermine si c'est débit/crédit
            transactionSortieCourant.setDateTransaction(dateTransfert);

            // Récupérer le revenu du client pour la validation du découvert
            BigDecimal revenu = null;
            try {
                revenu = historiqueRevenuService.getCurrentRevenuByClient(compteCourant.getIdClient());
            } catch (Exception e) {
                LOGGER.warning("Impossible de récupérer le revenu du client " + compteCourant.getIdClient());
            }

            com.example.centralizer.models.compteCourantDTO.Transaction resultSortieCourant = transactionCompteCourantService.createTransaction(transactionSortieCourant, compteCourant.getIdClient());
            if (resultSortieCourant == null) {
                throw new RuntimeException("Échec de la transaction de sortie sur le compte courant");
            }

            // 2. Créer transaction d'entrée sur le serveur CompteDepot
            com.example.centralizer.models.compteDepotDTO.Transaction transactionEntreeDepot = new com.example.centralizer.models.compteDepotDTO.Transaction();
            transactionEntreeDepot.setIdCompte(compteDepotId);
            transactionEntreeDepot.setIdTypeTransaction(TYPE_TRANSFERT_ENTRANT);
            transactionEntreeDepot.setMontant(montant); // Montant positif - le type détermine si c'est débit/crédit
            transactionEntreeDepot.setDateTransaction(dateTransfert);

            com.example.centralizer.models.compteDepotDTO.Transaction resultEntreeDepot = transactionCompteDepotService.createTransaction(transactionEntreeDepot);
            if (resultEntreeDepot == null) {
                throw new RuntimeException("Échec de la transaction d'entrée sur le compte dépôt");
            }

            // 3. Créer transfert manuellement sur le serveur CompteCourant (avec les transactions créées)
            com.example.centralizer.models.compteCourantDTO.Transfert transfertCourant = new com.example.centralizer.models.compteCourantDTO.Transfert();
            // Ne pas définir idTransfert - auto-increment
            transfertCourant.setEnvoyer(compteCourantId);
            transfertCourant.setReceveur(compteDepotId); // Référence vers le compte dépôt
            transfertCourant.setMontant(montant);
            transfertCourant.setDateTransfert(dateTransfert.toLocalDate());
            transfertCourant.setIdTransactionEnvoyeur(resultSortieCourant.getIdTransaction().toString());
            transfertCourant.setIdTransactionReceveur("INTER_" + resultEntreeDepot.getIdTransaction().toString()); // Référence externe

            com.example.centralizer.models.compteCourantDTO.Transfert resultTransfertCourant = transactionCompteCourantService.createTransfertInterSysteme(transfertCourant);

            // 4. Créer transfert manuellement sur le serveur CompteDepot (avec les transactions créées)
            com.example.centralizer.models.compteDepotDTO.Transfert transfertDepot = new com.example.centralizer.models.compteDepotDTO.Transfert();
            // Ne pas définir idTransfert - auto-increment
            transfertDepot.setEnvoyer(compteCourantId); // Référence vers le compte courant
            transfertDepot.setReceveur(compteDepotId);
            transfertDepot.setMontant(montant);
            transfertDepot.setDateTransfert(dateTransfert.toLocalDate()); // Conversion LocalDateTime -> LocalDate
            transfertDepot.setIdTransactionEnvoyeur("INTER_" + resultSortieCourant.getIdTransaction().toString()); // Référence externe
            transfertDepot.setIdTransactionReceveur(resultEntreeDepot.getIdTransaction().toString());

            com.example.centralizer.models.compteDepotDTO.Transfert resultTransfertDepot = transactionCompteDepotService.createTransfertInterSysteme(transfertDepot);

            boolean success = resultTransfertCourant != null && resultTransfertDepot != null;
            
            LOGGER.info("Transfert Courant -> Dépôt " + (success ? "réussi" : "échoué"));
            return success;

        } catch (Exception e) {
            LOGGER.severe("Erreur lors du transfert Courant -> Dépôt: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Validation des comptes
     */
    private boolean validerComptes(String typeCompteEnvoyeur, String compteEnvoyeur,
                                 String typeCompteReceveur, String compteReceveur) {
        try {
            // Vérifier que les types sont différents (inter-système)
            if (typeCompteEnvoyeur.equals(typeCompteReceveur)) {
                throw new IllegalArgumentException("Ce service ne gère que les transferts inter-systèmes (entre comptes de types différents)");
            }

            // Vérifier l'existence du compte envoyeur
            if ("compteDepot".equals(typeCompteEnvoyeur)) {
                Compte compte = compteDepotService.getCompteById(compteEnvoyeur);
                if (compte == null) {
                    throw new IllegalArgumentException("Compte dépôt envoyeur introuvable: " + compteEnvoyeur);
                }
            } else if ("compteCourant".equals(typeCompteEnvoyeur)) {
                CompteCourant compte = compteCourantService.getCompteById(compteEnvoyeur);
                if (compte == null) {
                    throw new IllegalArgumentException("Compte courant envoyeur introuvable: " + compteEnvoyeur);
                }
            }

            // Vérifier l'existence du compte receveur
            if ("compteDepot".equals(typeCompteReceveur)) {
                Compte compte = compteDepotService.getCompteById(compteReceveur);
                if (compte == null) {
                    throw new IllegalArgumentException("Compte dépôt receveur introuvable: " + compteReceveur);
                }
            } else if ("compteCourant".equals(typeCompteReceveur)) {
                CompteCourant compte = compteCourantService.getCompteById(compteReceveur);
                if (compte == null) {
                    throw new IllegalArgumentException("Compte courant receveur introuvable: " + compteReceveur);
                }
            }

            return true;
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la validation des comptes: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Vérification du solde du compte envoyeur
     */
    private boolean verifierSoldeEnvoyeur(String typeCompte, String compteId, BigDecimal montant) {
        try {
            BigDecimal soldeActuel;
            
            if ("compteDepot".equals(typeCompte)) {
                Compte compte = compteDepotService.getCompteById(compteId);
                soldeActuel = compte.getSolde();
            } else if ("compteCourant".equals(typeCompte)) {
                CompteCourant compte = compteCourantService.getCompteById(compteId);
                soldeActuel = compte.getSolde();
                // Pour les comptes courants, on peut avoir un découvert autorisé
                // On laisse le serveur CompteCourant gérer cette logique avec le revenu
                return true;
            } else {
                throw new IllegalArgumentException("Type de compte non supporté: " + typeCompte);
            }

            // Pour les comptes dépôt, vérification stricte du solde
            return soldeActuel.compareTo(montant) >= 0;
            
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la vérification du solde: " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupère les transferts récents inter-systèmes
     */
    public List<Map<String, Object>> getTransfertsRecents() {
        List<Map<String, Object>> transfertsRecents = new ArrayList<>();
        
        try {
            // Récupérer les transferts des comptes courants
            List<com.example.centralizer.models.compteCourantDTO.Transfert> transfertsCourant = transactionCompteCourantService.getAllTransferts();
            if (transfertsCourant != null) {
                for (com.example.centralizer.models.compteCourantDTO.Transfert transfert : transfertsCourant) {
                    // Vérifier si c'est un transfert inter-système
                    if (isTransfertInterSysteme(transfert.getEnvoyer(), transfert.getReceveur())) {
                        Map<String, Object> transfertInfo = new HashMap<>();
                        transfertInfo.put("type", "inter-systeme");
                        transfertInfo.put("envoyeur", transfert.getEnvoyer());
                        transfertInfo.put("receveur", transfert.getReceveur());
                        transfertInfo.put("montant", transfert.getMontant());
                        // Convertir LocalDate en LocalDateTime pour l'uniformité
                        transfertInfo.put("date", transfert.getDateTransfert().atStartOfDay());
                        transfertInfo.put("source", "CompteCourant");
                        transfertsRecents.add(transfertInfo);
                    }
                }
            }

            // Récupérer les transferts des comptes dépôt
            List<com.example.centralizer.models.compteDepotDTO.Transfert> transfertsDepot = transactionCompteDepotService.getAllTransferts();
            if (transfertsDepot != null) {
                for (com.example.centralizer.models.compteDepotDTO.Transfert transfert : transfertsDepot) {
                    // Vérifier si c'est un transfert inter-système
                    if (isTransfertInterSysteme(transfert.getEnvoyer(), transfert.getReceveur())) {
                        Map<String, Object> transfertInfo = new HashMap<>();
                        transfertInfo.put("type", "inter-systeme");
                        transfertInfo.put("envoyeur", transfert.getEnvoyer());
                        transfertInfo.put("receveur", transfert.getReceveur());
                        transfertInfo.put("montant", transfert.getMontant());
                        // Convertir LocalDate en LocalDateTime pour l'uniformité
                        transfertInfo.put("date", transfert.getDateTransfert().atStartOfDay());
                        transfertInfo.put("source", "CompteDepot");
                        transfertsRecents.add(transfertInfo);
                    }
                }
            }

            // Trier par date décroissante et limiter à 10
            transfertsRecents.sort((t1, t2) -> {
                LocalDateTime date1 = (LocalDateTime) t1.get("date");
                LocalDateTime date2 = (LocalDateTime) t2.get("date");
                return date2.compareTo(date1);
            });

            return transfertsRecents.subList(0, Math.min(10, transfertsRecents.size()));
            
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des transferts récents: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Vérifie si un transfert est inter-système en analysant les IDs des comptes
     */
    private boolean isTransfertInterSysteme(String compteEnvoyeur, String compteReceveur) {
        // Logique : si l'un commence par "C" (compte courant) et l'autre par "D" (compte dépôt), c'est inter-système
        boolean envoyeurCC = compteEnvoyeur.startsWith("C");
        boolean envoyeurCD = compteEnvoyeur.startsWith("D");
        boolean receveurCC = compteReceveur.startsWith("C");
        boolean receveurCD = compteReceveur.startsWith("D");
        
        return (envoyeurCC && receveurCD) || (envoyeurCD && receveurCC);
    }

    /**
     * Récupère l'historique des transferts pour un compte spécifique
     */
    public List<Map<String, Object>> getHistoriqueParCompte(String compteId, String typeCompte) {
        List<Map<String, Object>> historique = new ArrayList<>();
        
        try {
            if ("compteCourant".equals(typeCompte)) {
                List<com.example.centralizer.models.compteCourantDTO.Transfert> transferts = transactionCompteCourantService.getTransfertsByCompte(compteId);
                if (transferts != null) {
                    for (com.example.centralizer.models.compteCourantDTO.Transfert transfert : transferts) {
                        if (isTransfertInterSysteme(transfert.getEnvoyer(), transfert.getReceveur())) {
                            Map<String, Object> transfertInfo = new HashMap<>();
                            transfertInfo.put("envoyeur", transfert.getEnvoyer());
                            transfertInfo.put("receveur", transfert.getReceveur());
                            transfertInfo.put("montant", transfert.getMontant());
                            // Convertir LocalDate en LocalDateTime pour l'uniformité
                            transfertInfo.put("date", transfert.getDateTransfert().atStartOfDay());
                            transfertInfo.put("source", "CompteCourant");
                            historique.add(transfertInfo);
                        }
                    }
                }
            } else if ("compteDepot".equals(typeCompte)) {
                List<com.example.centralizer.models.compteDepotDTO.Transfert> transferts = transactionCompteDepotService.getTransfertsByCompte(compteId);
                if (transferts != null) {
                    for (com.example.centralizer.models.compteDepotDTO.Transfert transfert : transferts) {
                        if (isTransfertInterSysteme(transfert.getEnvoyer(), transfert.getReceveur())) {
                            Map<String, Object> transfertInfo = new HashMap<>();
                            transfertInfo.put("envoyeur", transfert.getEnvoyer());
                            transfertInfo.put("receveur", transfert.getReceveur());
                            transfertInfo.put("montant", transfert.getMontant());
                            // Convertir LocalDate en LocalDateTime pour l'uniformité
                            transfertInfo.put("date", transfert.getDateTransfert().atStartOfDay());
                            transfertInfo.put("source", "CompteDepot");
                            historique.add(transfertInfo);
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération de l'historique: " + e.getMessage());
        }
        
        return historique;
    }

    /**
     * Récupère tous les transferts inter-systèmes
     */
    public List<Map<String, Object>> getTousLesTransferts() {
        return getTransfertsRecents(); // Réutilise la même logique sans limite
    }
}