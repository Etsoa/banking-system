using ServeurCompteDepot.Models;
using Microsoft.EntityFrameworkCore;

namespace ServeurCompteDepot.Services
{
    public interface IHistoriqueSoldeService
    {
        Task<IEnumerable<HistoriqueSolde>> GetAllHistoriquesSoldeAsync();
        Task<HistoriqueSolde?> GetHistoriqueSoldeByIdAsync(int id);
        Task<IEnumerable<HistoriqueSolde>> GetHistoriquesSoldeByCompteAsync(int idCompte);
        Task<IEnumerable<HistoriqueSolde>> GetHistoriquesSoldeByTransactionAsync(int idTransaction);
        Task<IEnumerable<HistoriqueSolde>> GetHistoriquesSoldeByDateAsync(DateTime dateDebut, DateTime dateFin);
        Task<HistoriqueSolde> CreateHistoriqueSoldeAsync(HistoriqueSolde historiqueSolde);
        Task<HistoriqueSolde?> UpdateHistoriqueSoldeAsync(int id, HistoriqueSolde historiqueSolde);
        Task<bool> DeleteHistoriqueSoldeAsync(int id);
        Task<HistoriqueSolde?> GetDernierHistoriqueSoldeAsync(int idCompte);
    }

    public class HistoriqueSoldeService : IHistoriqueSoldeService
    {
        private readonly CompteDepotContext _context;

        public HistoriqueSoldeService(CompteDepotContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<HistoriqueSolde>> GetAllHistoriquesSoldeAsync()
        {
            return await _context.HistoriquesSolde
                .Include(h => h.Compte)
                .Include(h => h.Transaction)
                .OrderByDescending(h => h.DateChangement)
                .ToListAsync();
        }

        public async Task<HistoriqueSolde?> GetHistoriqueSoldeByIdAsync(int id)
        {
            return await _context.HistoriquesSolde
                .Include(h => h.Compte)
                .Include(h => h.Transaction)
                .FirstOrDefaultAsync(h => h.IdHistoriqueSolde == id);
        }

        public async Task<IEnumerable<HistoriqueSolde>> GetHistoriquesSoldeByCompteAsync(int idCompte)
        {
            return await _context.HistoriquesSolde
                .Include(h => h.Transaction)
                .Where(h => h.IdCompte == idCompte)
                .OrderByDescending(h => h.DateChangement)
                .ToListAsync();
        }

        public async Task<IEnumerable<HistoriqueSolde>> GetHistoriquesSoldeByTransactionAsync(int idTransaction)
        {
            return await _context.HistoriquesSolde
                .Include(h => h.Compte)
                .Where(h => h.IdTransaction == idTransaction)
                .OrderByDescending(h => h.DateChangement)
                .ToListAsync();
        }

        public async Task<IEnumerable<HistoriqueSolde>> GetHistoriquesSoldeByDateAsync(DateTime dateDebut, DateTime dateFin)
        {
            return await _context.HistoriquesSolde
                .Include(h => h.Compte)
                .Include(h => h.Transaction)
                .Where(h => h.DateChangement >= dateDebut && h.DateChangement <= dateFin)
                .OrderByDescending(h => h.DateChangement)
                .ToListAsync();
        }

        public async Task<HistoriqueSolde> CreateHistoriqueSoldeAsync(HistoriqueSolde historiqueSolde)
        {
            _context.HistoriquesSolde.Add(historiqueSolde);
            await _context.SaveChangesAsync();
            return historiqueSolde;
        }

        public async Task<HistoriqueSolde?> UpdateHistoriqueSoldeAsync(int id, HistoriqueSolde historiqueSolde)
        {
            var existingHistorique = await _context.HistoriquesSolde.FindAsync(id);
            if (existingHistorique == null) return null;

            existingHistorique.Montant = historiqueSolde.Montant;
            existingHistorique.DateChangement = historiqueSolde.DateChangement;
            existingHistorique.IdCompte = historiqueSolde.IdCompte;
            existingHistorique.IdTransaction = historiqueSolde.IdTransaction;

            await _context.SaveChangesAsync();
            return existingHistorique;
        }

        public async Task<bool> DeleteHistoriqueSoldeAsync(int id)
        {
            var historiqueSolde = await _context.HistoriquesSolde.FindAsync(id);
            if (historiqueSolde == null) return false;

            _context.HistoriquesSolde.Remove(historiqueSolde);
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<HistoriqueSolde?> GetDernierHistoriqueSoldeAsync(int idCompte)
        {
            return await _context.HistoriquesSolde
                .Include(h => h.Transaction)
                .Where(h => h.IdCompte == idCompte)
                .OrderByDescending(h => h.DateChangement)
                .FirstOrDefaultAsync();
        }
    }
}