using ServeurCompteDepot.Models;
using Microsoft.EntityFrameworkCore;

namespace ServeurCompteDepot.Services
{
    public interface ITransfertService
    {
        Task<IEnumerable<Transfert>> GetAllTransfertsAsync();
        Task<Transfert?> GetTransfertByIdAsync(int id);
        Task<IEnumerable<Transfert>> GetTransfertsByCompteEnvoyeurAsync(int idCompteEnvoyeur);
        Task<IEnumerable<Transfert>> GetTransfertsByCompteReceveurAsync(int idCompteReceveur);
        Task<IEnumerable<Transfert>> GetTransfertsByDateAsync(DateTime dateDebut, DateTime dateFin);
        Task<Transfert> CreateTransfertAsync(Transfert transfert);
        Task<Transfert?> UpdateTransfertAsync(int id, Transfert transfert);
        Task<bool> DeleteTransfertAsync(int id);
        Task<decimal> GetTotalTransfertsEnvoyesAsync(int idCompte);
        Task<decimal> GetTotalTransfertsRecusAsync(int idCompte);
    }

    public class TransfertService : ITransfertService
    {
        private readonly CompteDepotContext _context;

        public TransfertService(CompteDepotContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<Transfert>> GetAllTransfertsAsync()
        {
            return await _context.Transferts
                .Include(t => t.CompteEnvoyeur)
                .Include(t => t.CompteReceveur)
                .Include(t => t.Transactions)
                .OrderByDescending(t => t.DateTransfert)
                .ToListAsync();
        }

        public async Task<Transfert?> GetTransfertByIdAsync(int id)
        {
            return await _context.Transferts
                .Include(t => t.CompteEnvoyeur)
                .Include(t => t.CompteReceveur)
                .Include(t => t.Transactions)
                .FirstOrDefaultAsync(t => t.IdTransfert == id);
        }

        public async Task<IEnumerable<Transfert>> GetTransfertsByCompteEnvoyeurAsync(int idCompteEnvoyeur)
        {
            return await _context.Transferts
                .Include(t => t.CompteReceveur)
                .Include(t => t.Transactions)
                .Where(t => t.Envoyer == idCompteEnvoyeur)
                .OrderByDescending(t => t.DateTransfert)
                .ToListAsync();
        }

        public async Task<IEnumerable<Transfert>> GetTransfertsByCompteReceveurAsync(int idCompteReceveur)
        {
            return await _context.Transferts
                .Include(t => t.CompteEnvoyeur)
                .Include(t => t.Transactions)
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

        public async Task<Transfert> CreateTransfertAsync(Transfert transfert)
        {
            _context.Transferts.Add(transfert);
            await _context.SaveChangesAsync();
            return transfert;
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

        public async Task<decimal> GetTotalTransfertsEnvoyesAsync(int idCompte)
        {
            return await _context.Transferts
                .Where(t => t.Envoyer == idCompte)
                .SumAsync(t => t.Montant);
        }

        public async Task<decimal> GetTotalTransfertsRecusAsync(int idCompte)
        {
            return await _context.Transferts
                .Where(t => t.Receveur == idCompte)
                .SumAsync(t => t.Montant);
        }
    }
}