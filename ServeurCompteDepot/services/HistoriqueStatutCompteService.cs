using ServeurCompteDepot.Models;
using Microsoft.EntityFrameworkCore;

namespace ServeurCompteDepot.Services
{
    public interface IHistoriqueStatutCompteService
    {
        Task<IEnumerable<HistoriqueStatutCompte>> GetAllHistoriquesStatutCompteAsync();
        Task<HistoriqueStatutCompte?> GetHistoriqueStatutCompteByIdAsync(int id);
        Task<IEnumerable<HistoriqueStatutCompte>> GetHistoriquesStatutCompteByCompteAsync(string idCompte);
        Task<IEnumerable<HistoriqueStatutCompte>> GetHistoriquesStatutCompteByTypeAsync(int idTypeStatutCompte);
        Task<IEnumerable<HistoriqueStatutCompte>> GetHistoriquesStatutCompteByDateAsync(DateTime dateDebut, DateTime dateFin);
        Task<HistoriqueStatutCompte> CreateHistoriqueStatutCompteAsync(HistoriqueStatutCompte historiqueStatutCompte);
        Task<HistoriqueStatutCompte?> UpdateHistoriqueStatutCompteAsync(int id, HistoriqueStatutCompte historiqueStatutCompte);
        Task<bool> DeleteHistoriqueStatutCompteAsync(int id);
        Task<HistoriqueStatutCompte?> GetStatutActuelCompteAsync(string idCompte);
        Task<IEnumerable<HistoriqueStatutCompte>> GetHistoriqueCompletAsync(string idCompte);
    }

    public class HistoriqueStatutCompteService : IHistoriqueStatutCompteService
    {
        private readonly CompteDepotContext _context;

        public HistoriqueStatutCompteService(CompteDepotContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<HistoriqueStatutCompte>> GetAllHistoriquesStatutCompteAsync()
        {
            return await _context.HistoriquesStatutCompte
                .Include(h => h.Compte)
                .Include(h => h.TypeStatutCompte)
                .OrderByDescending(h => h.DateChangement)
                .ToListAsync();
        }

        public async Task<HistoriqueStatutCompte?> GetHistoriqueStatutCompteByIdAsync(int id)
        {
            return await _context.HistoriquesStatutCompte
                .Include(h => h.Compte)
                .Include(h => h.TypeStatutCompte)
                .FirstOrDefaultAsync(h => h.IdHistoriqueStatutCompte == id);
        }

        public async Task<IEnumerable<HistoriqueStatutCompte>> GetHistoriquesStatutCompteByCompteAsync(string idCompte)
        {
            return await _context.HistoriquesStatutCompte
                .Include(h => h.TypeStatutCompte)
                .Where(h => h.IdCompte == idCompte)
                .OrderByDescending(h => h.DateChangement)
                .ToListAsync();
        }

        public async Task<IEnumerable<HistoriqueStatutCompte>> GetHistoriquesStatutCompteByTypeAsync(int idTypeStatutCompte)
        {
            return await _context.HistoriquesStatutCompte
                .Include(h => h.Compte)
                .Where(h => h.IdTypeStatutCompte == idTypeStatutCompte)
                .OrderByDescending(h => h.DateChangement)
                .ToListAsync();
        }

        public async Task<IEnumerable<HistoriqueStatutCompte>> GetHistoriquesStatutCompteByDateAsync(DateTime dateDebut, DateTime dateFin)
        {
            return await _context.HistoriquesStatutCompte
                .Include(h => h.Compte)
                .Include(h => h.TypeStatutCompte)
                .Where(h => h.DateChangement >= dateDebut && h.DateChangement <= dateFin)
                .OrderByDescending(h => h.DateChangement)
                .ToListAsync();
        }

        public async Task<HistoriqueStatutCompte> CreateHistoriqueStatutCompteAsync(HistoriqueStatutCompte historiqueStatutCompte)
        {
            _context.HistoriquesStatutCompte.Add(historiqueStatutCompte);
            await _context.SaveChangesAsync();
            return historiqueStatutCompte;
        }

        public async Task<HistoriqueStatutCompte?> UpdateHistoriqueStatutCompteAsync(int id, HistoriqueStatutCompte historiqueStatutCompte)
        {
            var existingHistorique = await _context.HistoriquesStatutCompte.FindAsync(id);
            if (existingHistorique == null) return null;

            existingHistorique.DateChangement = historiqueStatutCompte.DateChangement;
            existingHistorique.IdCompte = historiqueStatutCompte.IdCompte;
            existingHistorique.IdTypeStatutCompte = historiqueStatutCompte.IdTypeStatutCompte;

            await _context.SaveChangesAsync();
            return existingHistorique;
        }

        public async Task<bool> DeleteHistoriqueStatutCompteAsync(int id)
        {
            var historiqueStatutCompte = await _context.HistoriquesStatutCompte.FindAsync(id);
            if (historiqueStatutCompte == null) return false;

            _context.HistoriquesStatutCompte.Remove(historiqueStatutCompte);
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<HistoriqueStatutCompte?> GetStatutActuelCompteAsync(string idCompte)
        {
            return await _context.HistoriquesStatutCompte
                .Include(h => h.TypeStatutCompte)
                .Where(h => h.IdCompte == idCompte)
                .OrderByDescending(h => h.DateChangement)
                .FirstOrDefaultAsync();
        }

        public async Task<IEnumerable<HistoriqueStatutCompte>> GetHistoriqueCompletAsync(string idCompte)
        {
            return await _context.HistoriquesStatutCompte
                .Include(h => h.TypeStatutCompte)
                .Where(h => h.IdCompte == idCompte)
                .OrderBy(h => h.DateChangement)
                .ToListAsync();
        }
    }
}