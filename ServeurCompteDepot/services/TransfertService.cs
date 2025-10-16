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
        Task<IEnumerable<Transfert>> GetTransfertsByDateAsync(DateTime dateDebut, DateTime dateFin);
        Task<Transfert> CreateTransfertAsync(string compteEnvoyeur, string compteReceveur, decimal montant);
        Task<Transfert?> UpdateTransfertAsync(int id, Transfert transfert);
        Task<bool> DeleteTransfertAsync(int id);
        Task<decimal> GetTotalTransfertsSortantsAsync(string idCompte);
        Task<decimal> GetTotalTransfertsEntrantsAsync(string idCompte);
        Task<int> GetNombreTransfertsSortantsAsync(string idCompte);
        Task<int> GetNombreTransfertsEntrantsAsync(string idCompte);
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
                .Include(t => t.Transactions)
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

        public async Task<IEnumerable<Transfert>> GetTransfertsByDateAsync(DateTime dateDebut, DateTime dateFin)
        {
            return await _context.Transferts
                .Include(t => t.CompteEnvoyeur)
                .Include(t => t.CompteReceveur)
                .Where(t => t.DateTransfert >= dateDebut.Date && t.DateTransfert <= dateFin.Date)
                .OrderByDescending(t => t.DateTransfert)
                .ToListAsync();
        }

        public async Task<Transfert> CreateTransfertAsync(string compteEnvoyeur, string compteReceveur, decimal montant)
        {
            // Vérifier que les comptes existent
            var compteEnv = await _context.Comptes.FirstOrDefaultAsync(c => c.IdCompte == compteEnvoyeur);
            var compteRec = await _context.Comptes.FirstOrDefaultAsync(c => c.IdCompte == compteReceveur);
            
            if (compteEnv == null)
                throw new ArgumentException($"Le compte envoyeur {compteEnvoyeur} n'existe pas");
            
            if (compteRec == null)
                throw new ArgumentException($"Le compte receveur {compteReceveur} n'existe pas");
            
            // Vérifier que le solde est suffisant (AUCUN découvert autorisé pour les comptes dépôt)
            if (compteEnv.Solde < montant)
                throw new InvalidOperationException($"Solde insuffisant pour le compte {compteEnvoyeur}. Solde actuel: {compteEnv.Solde}, Montant demandé: {montant}");
            
            // Déléguer la création du transfert au TransactionService
            return await _transactionService.CreateTransfertAsync(compteEnvoyeur, compteReceveur, montant);
        }

        public async Task<Transfert?> UpdateTransfertAsync(int id, Transfert transfert)
        {
            var existingTransfert = await _context.Transferts.FindAsync(id);
            if (existingTransfert == null) return null;

            existingTransfert.DateTransfert = transfert.DateTransfert;
            existingTransfert.Montant = transfert.Montant;
            existingTransfert.Envoyer = transfert.Envoyer;
            existingTransfert.Receveur = transfert.Receveur;

            await _context.SaveChangesAsync();
            return existingTransfert;
        }

        public async Task<bool> DeleteTransfertAsync(int id)
        {
            var transfert = await _context.Transferts.FindAsync(id);
            if (transfert == null) return false;

            _context.Transferts.Remove(transfert);
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<decimal> GetTotalTransfertsSortantsAsync(string idCompte)
        {
            return await _context.Transferts
                .Where(t => t.Envoyer == idCompte)
                .SumAsync(t => t.Montant);
        }

        public async Task<decimal> GetTotalTransfertsEntrantsAsync(string idCompte)
        {
            return await _context.Transferts
                .Where(t => t.Receveur == idCompte)
                .SumAsync(t => t.Montant);
        }

        public async Task<int> GetNombreTransfertsSortantsAsync(string idCompte)
        {
            return await _context.Transferts
                .Where(t => t.Envoyer == idCompte)
                .CountAsync();
        }

        public async Task<int> GetNombreTransfertsEntrantsAsync(string idCompte)
        {
            return await _context.Transferts
                .Where(t => t.Receveur == idCompte)
                .CountAsync();
        }
    }
}