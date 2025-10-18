using ServeurCompteDepot.Models;
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
        Task<Transfert> CreateTransfertAsync(string compteEnvoyeur, string compteReceveur, decimal montant);
        Task<decimal> GetTotalTransfertsSortantsAsync(string compteId);
        Task<decimal> GetTotalTransfertsEntrantsAsync(string compteId);
        Task<int> GetNombreTransfertsSortantsAsync(string compteId);
        Task<int> GetNombreTransfertsEntrantsAsync(string compteId);
    }

    public class TransfertService : ITransfertService
    {
        private readonly CompteDepotContext _context;
        private readonly ITransactionService _transactionService;
        private readonly IHistoriqueSoldeService _historiqueSoldeService;

        public TransfertService(CompteDepotContext context, ITransactionService transactionService, IHistoriqueSoldeService historiqueSoldeService)
        {
            _context = context;
            _transactionService = transactionService;
            _historiqueSoldeService = historiqueSoldeService;
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

        public async Task<Transfert> CreateTransfertAsync(string compteEnvoyeur, string compteReceveur, decimal montant)
        {
            // Vérifier que les comptes existent
            var compteEnv = await _context.Comptes.FirstOrDefaultAsync(c => c.IdCompte == compteEnvoyeur);
            var compteRec = await _context.Comptes.FirstOrDefaultAsync(c => c.IdCompte == compteReceveur);
            
            // Vérifier que le solde est suffisant (AUCUN découvert autorisé pour les comptes dépôt)
            if (compteEnv.Solde < montant)
                throw new InvalidOperationException($"Solde insuffisant pour le compte {compteEnvoyeur}. Solde actuel: {compteEnv.Solde}, Montant demandé: {montant}");
            
            // Déléguer la création du transfert au TransactionService
            return await _transactionService.CreateTransfertAsync(compteEnvoyeur, compteReceveur, montant);
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
    }
}