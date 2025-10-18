using ServeurCompteDepot.Models;
using Microsoft.EntityFrameworkCore;

namespace ServeurCompteDepot.Services
{
    public interface IHistoriqueSoldeService
    {
        Task<IEnumerable<HistoriqueSolde>> GetAllHistoriquesSoldeAsync();
        Task<IEnumerable<HistoriqueSolde>> GetHistoriquesSoldeByCompteAsync(string idCompte);
        Task<HistoriqueSolde> CreateHistoriqueSoldeAsync(string idCompte, int idTransaction, decimal montant);
        Task<HistoriqueSolde?> GetDernierHistoriqueSoldeAsync(string idCompte);
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

        public async Task<IEnumerable<HistoriqueSolde>> GetHistoriquesSoldeByCompteAsync(string idCompte)
        {
            return await _context.HistoriquesSolde
                .Include(h => h.Transaction)
                .Where(h => h.IdCompte == idCompte)
                .OrderByDescending(h => h.DateChangement)
                .ToListAsync();
        }

        public async Task<HistoriqueSolde> CreateHistoriqueSoldeAsync(string idCompte, int idTransaction, decimal montant, DateTime dateChangement)
        {
            var historiqueSolde = new HistoriqueSolde
            {
                IdCompte = idCompte,
                IdTransaction = idTransaction,
                Montant = montant,
                DateChangement = dateChangement
            };
            
            _context.HistoriquesSolde.Add(historiqueSolde);
            await _context.SaveChangesAsync();
            return historiqueSolde;
        }

        public async Task<HistoriqueSolde?> GetDernierHistoriqueSoldeAsync(string idCompte)
        {
            return await _context.HistoriquesSolde
                .Include(h => h.Transaction)
                .Where(h => h.IdCompte == idCompte)
                .OrderByDescending(h => h.DateChangement)
                .FirstOrDefaultAsync();
        }
    }
}