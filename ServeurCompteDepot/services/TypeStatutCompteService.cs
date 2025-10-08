using ServeurCompteDepot.models;
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
        Task<TypeStatutCompte> CreateTypeStatutCompteAsync(TypeStatutCompte typeStatutCompte);
        Task<TypeStatutCompte?> UpdateTypeStatutCompteAsync(int id, TypeStatutCompte typeStatutCompte);
        Task<bool> DeleteTypeStatutCompteAsync(int id);
        Task<bool> ToggleTypeStatutCompteAsync(int id);
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

        public async Task<TypeStatutCompte> CreateTypeStatutCompteAsync(TypeStatutCompte typeStatutCompte)
        {
            _context.TypesStatutCompte.Add(typeStatutCompte);
            await _context.SaveChangesAsync();
            return typeStatutCompte;
        }

        public async Task<TypeStatutCompte?> UpdateTypeStatutCompteAsync(int id, TypeStatutCompte typeStatutCompte)
        {
            var existingType = await _context.TypesStatutCompte.FindAsync(id);
            if (existingType == null) return null;

            existingType.Libelle = typeStatutCompte.Libelle;
            existingType.Actif = typeStatutCompte.Actif;

            await _context.SaveChangesAsync();
            return existingType;
        }

        public async Task<bool> DeleteTypeStatutCompteAsync(int id)
        {
            var typeStatutCompte = await _context.TypesStatutCompte.FindAsync(id);
            if (typeStatutCompte == null) return false;

            _context.TypesStatutCompte.Remove(typeStatutCompte);
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<bool> ToggleTypeStatutCompteAsync(int id)
        {
            var typeStatutCompte = await _context.TypesStatutCompte.FindAsync(id);
            if (typeStatutCompte == null) return false;

            typeStatutCompte.Actif = !typeStatutCompte.Actif;
            await _context.SaveChangesAsync();
            return true;
        }
    }
}