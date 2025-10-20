using ServeurCompteDepot.Models;
using ServeurCompteDepot.models;
using Microsoft.EntityFrameworkCore;

namespace ServeurCompteDepot.Services
{
    public interface ITransfertService
    {
        Task<IEnumerable<Transfert>> GetAllTransfertsAsync();
        Task<Transfert?> GetTransfertByIdAsync(int id);
        Task<IEnumerable<Transfert>> GetTransfertsByCompteAsync(string idCompte);
        Task<IEnumerable<Transfert>> GetTransfertsByCompteEnvoyeurAsync(string idCompteEnvoyeur);
        Task<IEnumerable<Transfert>> GetTransfertsByCompteReceveurAsync(string idCompteReceveur);
        Task<Transfert> CreateTransfertAsync(string compteEnvoyeur, string compteReceveur, decimal montant, DateTime dateTransfert);
        Task<decimal> GetTotalTransfertsSortantsAsync(string compteId);
        Task<decimal> GetTotalTransfertsEntrantsAsync(string compteId);
        Task<int> GetNombreTransfertsSortantsAsync(string compteId);
        Task<int> GetNombreTransfertsEntrantsAsync(string compteId);
        // Nouvelles méthodes avec frais
        Task<IEnumerable<TransfertAvecFrais>> GetAllTransfertsAvecFraisAsync();
        Task<IEnumerable<TransfertAvecFrais>> GetTransfertsByCompteAvecFraisAsync(string idCompte);
    }

    public class TransfertService : ITransfertService
    {
        private readonly CompteDepotContext _context;
        private readonly ITransactionService _transactionService;
        private readonly IHistoriqueSoldeService _historiqueSoldeService;
        private readonly IFraisService _fraisService;

        public TransfertService(CompteDepotContext context, ITransactionService transactionService, IHistoriqueSoldeService historiqueSoldeService, IFraisService fraisService)
        {
            _context = context;
            _transactionService = transactionService;
            _historiqueSoldeService = historiqueSoldeService;
            _fraisService = fraisService;
        }

        public async Task<IEnumerable<Transfert>> GetAllTransfertsAsync()
        {
            return await _context.Transferts
                .Include(t => t.CompteEnvoyeur)
                .Include(t => t.CompteReceveur)
                .OrderByDescending(t => t.DateTransfert)
                .ToListAsync();
        }

        public async Task<Transfert?> GetTransfertByIdAsync(int id)
        {
            return await _context.Transferts
                .Include(t => t.CompteEnvoyeur)
                .Include(t => t.CompteReceveur)
                .FirstOrDefaultAsync(t => t.IdTransfert == id);
        }

        public async Task<IEnumerable<Transfert>> GetTransfertsByCompteAsync(string idCompte)
        {
            return await _context.Transferts
                .Include(t => t.CompteEnvoyeur)
                .Include(t => t.CompteReceveur)
                .Where(t => t.Envoyer == idCompte || t.Receveur == idCompte)
                .OrderByDescending(t => t.DateTransfert)
                .ToListAsync();
        }

        public async Task<IEnumerable<Transfert>> GetTransfertsByCompteEnvoyeurAsync(string idCompteEnvoyeur)
        {
            return await _context.Transferts
                .Include(t => t.CompteReceveur)
                .Where(t => t.Envoyer == idCompteEnvoyeur)
                .OrderByDescending(t => t.DateTransfert)
                .ToListAsync();
        }

        public async Task<IEnumerable<Transfert>> GetTransfertsByCompteReceveurAsync(string idCompteReceveur)
        {
            return await _context.Transferts
                .Include(t => t.CompteEnvoyeur)
                .Where(t => t.Receveur == idCompteReceveur)
                .OrderByDescending(t => t.DateTransfert)
                .ToListAsync();
        }

        public async Task<Transfert> CreateTransfertAsync(string compteEnvoyeur, string compteReceveur, decimal montant, DateTime dateTransfert)
        {
            using var transaction = await _context.Database.BeginTransactionAsync();
            
            try
            {
                // Vérifier que les comptes existent
                var compteEnv = await _context.Comptes.FirstOrDefaultAsync(c => c.IdCompte == compteEnvoyeur);
                var compteRec = await _context.Comptes.FirstOrDefaultAsync(c => c.IdCompte == compteReceveur);
                
                if (compteEnv == null)
                    throw new InvalidOperationException($"Compte envoyeur {compteEnvoyeur} introuvable");
                
                if (compteRec == null)
                    throw new InvalidOperationException($"Compte receveur {compteReceveur} introuvable");
                
                // Vérifier que le solde est suffisant (pas de découvert autorisé pour les comptes dépôt)
                if (compteEnv.Solde < montant)
                    throw new InvalidOperationException($"Solde insuffisant. Solde actuel: {compteEnv.Solde}, Montant demandé: {montant}");
                
                // Créer la transaction sortante (débit)
                var transactionSortante = new Transaction
                {
                    IdCompte = compteEnvoyeur,
                    IdTypeTransaction = 4, // Virement sortant
                    Montant = montant,
                    DateTransaction = DateTime.SpecifyKind(dateTransfert, DateTimeKind.Utc)
                };
                
                _context.Transactions.Add(transactionSortante);
                await _context.SaveChangesAsync();
                
                // Créer la transaction entrante (crédit)
                var transactionEntrante = new Transaction
                {
                    IdCompte = compteReceveur,
                    IdTypeTransaction = 3, // Virement entrant
                    Montant = montant,
                    DateTransaction = DateTime.SpecifyKind(dateTransfert, DateTimeKind.Utc)
                };
                
                _context.Transactions.Add(transactionEntrante);
                await _context.SaveChangesAsync();
                
                // Créer le transfert
                var transfert = new Transfert
                {
                    DateTransfert = dateTransfert.Date, // Utiliser seulement la date
                    IdTransactionEnvoyeur = transactionSortante.IdTransaction.ToString(),
                    IdTransactionReceveur = transactionEntrante.IdTransaction.ToString(),
                    Montant = montant,
                    Envoyer = compteEnvoyeur,
                    Receveur = compteReceveur
                };
                
                _context.Transferts.Add(transfert);
                await _context.SaveChangesAsync();
                
                // Mettre à jour les soldes
                compteEnv.Solde -= montant;
                compteRec.Solde += montant;
                
                // Créer les historiques de solde
                var histoSoldeEnvoyeur = new HistoriqueSolde
                {
                    IdCompte = compteEnvoyeur,
                    IdTransaction = transactionSortante.IdTransaction,
                    Montant = compteEnv.Solde,
                    DateChangement = DateTime.SpecifyKind(dateTransfert, DateTimeKind.Utc)
                };
                
                var histoSoldeReceveur = new HistoriqueSolde
                {
                    IdCompte = compteReceveur,
                    IdTransaction = transactionEntrante.IdTransaction,
                    Montant = compteRec.Solde,
                    DateChangement = DateTime.SpecifyKind(dateTransfert, DateTimeKind.Utc)
                };
                
                _context.HistoriquesSolde.Add(histoSoldeEnvoyeur);
                _context.HistoriquesSolde.Add(histoSoldeReceveur);
                
                await _context.SaveChangesAsync();
                await transaction.CommitAsync();
                
                return transfert;
            }
            catch
            {
                await transaction.RollbackAsync();
                throw;
            }
        }

        public async Task<decimal> GetTotalTransfertsSortantsAsync(string compteId)
        {
            return await _context.Transferts
                .Where(t => t.Envoyer == compteId)
                .SumAsync(t => t.Montant);
        }

        public async Task<decimal> GetTotalTransfertsEntrantsAsync(string compteId)
        {
            return await _context.Transferts
                .Where(t => t.Receveur == compteId)
                .SumAsync(t => t.Montant);
        }

        public async Task<int> GetNombreTransfertsSortantsAsync(string compteId)
        {
            return await _context.Transferts
                .Where(t => t.Envoyer == compteId)
                .CountAsync();
        }

        public async Task<int> GetNombreTransfertsEntrantsAsync(string compteId)
        {
            return await _context.Transferts
                .Where(t => t.Receveur == compteId)
                .CountAsync();
        }

        /// <summary>
        /// Récupère tous les transferts avec les frais associés
        /// </summary>
        public async Task<IEnumerable<TransfertAvecFrais>> GetAllTransfertsAvecFraisAsync()
        {
            var transferts = await GetAllTransfertsAsync();
            return await EnrichirTransfertsAvecFraisAsync(transferts);
        }

        /// <summary>
        /// Récupère les transferts d'un compte avec les frais associés
        /// </summary>
        public async Task<IEnumerable<TransfertAvecFrais>> GetTransfertsByCompteAvecFraisAsync(string idCompte)
        {
            var transferts = await GetTransfertsByCompteAsync(idCompte);
            return await EnrichirTransfertsAvecFraisAsync(transferts);
        }

        /// <summary>
        /// Enrichit une liste de transferts avec les informations sur les frais
        /// </summary>
        private async Task<IEnumerable<TransfertAvecFrais>> EnrichirTransfertsAvecFraisAsync(IEnumerable<Transfert> transferts)
        {
            var transfertsAvecFrais = new List<TransfertAvecFrais>();

            foreach (var transfert in transferts)
            {
                var transfertAvecFrais = new TransfertAvecFrais(transfert);

                try
                {
                    // Récupérer la transaction de sortie (virement sortant)
                    if (!string.IsNullOrEmpty(transfert.IdTransactionEnvoyeur) && 
                        int.TryParse(transfert.IdTransactionEnvoyeur, out int idTransactionEnvoyeur))
                    {
                        var transactionEnvoyeur = await _context.Transactions
                            .FirstOrDefaultAsync(t => t.IdTransaction == idTransactionEnvoyeur);

                        if (transactionEnvoyeur != null)
                        {
                            // Récupérer les frais applicables pour cette transaction de type "Virement sortant"
                            var frais = await _fraisService.FindCurrentFraisAsync(
                                "Virement sortant",
                                transfert.Montant,
                                transactionEnvoyeur.DateTransaction
                            );

                            if (frais != null)
                            {
                                // Le montant des frais est directement la valeur du frais
                                var montantFrais = frais.Valeur;

                                transfertAvecFrais.FraisEnvoyeur = montantFrais;
                                transfertAvecFrais.LibelleFraisEnvoyeur = frais.Nom;
                                transfertAvecFrais.MontantTotalEnvoyeur = transfert.Montant + montantFrais;
                            }
                            else
                            {
                                // Pas de frais applicables
                                transfertAvecFrais.FraisEnvoyeur = 0;
                                transfertAvecFrais.LibelleFraisEnvoyeur = "Aucun frais";
                                transfertAvecFrais.MontantTotalEnvoyeur = transfert.Montant;
                            }
                        }
                    }

                    // Si pas de transaction trouvée, initialiser avec des valeurs par défaut
                    if (string.IsNullOrEmpty(transfertAvecFrais.LibelleFraisEnvoyeur))
                    {
                        transfertAvecFrais.FraisEnvoyeur = 0;
                        transfertAvecFrais.LibelleFraisEnvoyeur = "Non calculé";
                        transfertAvecFrais.MontantTotalEnvoyeur = transfert.Montant;
                    }
                }
                catch (Exception ex)
                {
                    // En cas d'erreur, initialiser avec des valeurs par défaut
                    Console.WriteLine($"Erreur lors du calcul des frais pour le transfert {transfert.IdTransfert}: {ex.Message}");
                    transfertAvecFrais.FraisEnvoyeur = 0;
                    transfertAvecFrais.LibelleFraisEnvoyeur = "Erreur calcul";
                    transfertAvecFrais.MontantTotalEnvoyeur = transfert.Montant;
                }

                transfertsAvecFrais.Add(transfertAvecFrais);
            }

            return transfertsAvecFrais;
        }
    }
}