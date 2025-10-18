using ServeurCompteDepot.Models;
using Microsoft.EntityFrameworkCore;

namespace ServeurCompteDepot.Services
{
    public interface ITypeStatutCompteService
    {
        Task<IEnumerable<TypeStatutCompte>> GetAllTypesStatutCompteAsync();
        Task<TypeStatutCompte?> GetTypeStatutCompteByIdAsync(int id);
        Task<IEnumerable<TypeStatutCompte>> GetTypesStatutCompteActifsAsync();
        Task<TypeStatutCompte?> GetTypeStatutCompteByLibelleAsync(string libelle);
    }

    public class TypeStatutCompteService : ITypeStatutCompteService
    {
        private readonly CompteDepotContext _context;

        public TypeStatutCompteService(CompteDepotContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<TypeStatutCompte>> GetAllTypesStatutCompteAsync()
        {
            return await _context.TypesStatutCompte
                .Include(ts => ts.HistoriquesStatut)
                .ToListAsync();
        }

        public async Task<TypeStatutCompte?> GetTypeStatutCompteByIdAsync(int id)
        {
            return await _context.TypesStatutCompte
                .Include(ts => ts.HistoriquesStatut)
                .FirstOrDefaultAsync(ts => ts.IdTypeStatutCompte == id);
        }

        public async Task<IEnumerable<TypeStatutCompte>> GetTypesStatutCompteActifsAsync()
        {
            return await _context.TypesStatutCompte
                .Where(ts => ts.Actif)
                .ToListAsync();
        }

        public async Task<TypeStatutCompte?> GetTypeStatutCompteByLibelleAsync(string libelle)
        {
            return await _context.TypesStatutCompte
                .FirstOrDefaultAsync(ts => ts.Libelle.ToLower() == libelle.ToLower());
        }
    }
}