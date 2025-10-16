using ServeurCompteDepot.Models;
using Microsoft.EntityFrameworkCore;

namespace ServeurCompteDepot.Services
{
    public interface ICompteService
    {
        Task<IEnumerable<Compte>> GetAllComptesAsync();
        Task<IEnumerable<CompteAvecStatut>> GetAllComptesAvecStatutAsync();
        Task<Compte?> GetCompteByIdAsync(string id);
        Task<CompteAvecStatut?> GetCompteAvecStatutByIdAsync(string id);
        Task<IEnumerable<Compte>> GetComptesByClientAsync(int idClient);
        Task<Compte> CreateCompteAsync(Compte compte);
        Task<Compte?> UpdateCompteAsync(string id, Compte compte);
        Task<bool> DeleteCompteAsync(string id);
        Task<decimal> GetSoldeAsync(string idCompte);
        Task<bool> UpdateSoldeAsync(string idCompte, decimal nouveauSolde);
    }

    public class CompteService : ICompteService
    {
        private readonly CompteDepotContext _context;

        public CompteService(CompteDepotContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<Compte>> GetAllComptesAsync()
        {
            return await _context.Comptes
                .Include(c => c.Transactions)
                .Include(c => c.HistoriquesSolde)
                .ToListAsync();
        }

        public async Task<Compte?> GetCompteByIdAsync(string id)
        {
            return await _context.Comptes
                .Include(c => c.Transactions)
                .Include(c => c.HistoriquesSolde)
                .Include(c => c.HistoriquesStatut)
                .FirstOrDefaultAsync(c => c.IdCompte == id);
        }

        public async Task<IEnumerable<Compte>> GetComptesByClientAsync(int idClient)
        {
            return await _context.Comptes
                .Where(c => c.IdClient == idClient)
                .Include(c => c.Transactions)
                .ToListAsync();
        }

        public async Task<Compte> CreateCompteAsync(Compte compte)
        {
            _context.Comptes.Add(compte);
            await _context.SaveChangesAsync();
            return compte;
        }

        public async Task<Compte?> UpdateCompteAsync(string id, Compte compte)
        {
            var existingCompte = await _context.Comptes.FirstOrDefaultAsync(c => c.IdCompte == id);
            if (existingCompte == null) return null;

            existingCompte.IdClient = compte.IdClient;
            existingCompte.Solde = compte.Solde;

            await _context.SaveChangesAsync();
            return existingCompte;
        }

        public async Task<bool> DeleteCompteAsync(string id)
        {
            var compte = await _context.Comptes.FirstOrDefaultAsync(c => c.IdCompte == id);
            if (compte == null) return false;

            _context.Comptes.Remove(compte);
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<decimal> GetSoldeAsync(string idCompte)
        {
            var compte = await _context.Comptes.FirstOrDefaultAsync(c => c.IdCompte == idCompte);
            return compte?.Solde ?? 0;
        }

        public async Task<bool> UpdateSoldeAsync(string idCompte, decimal nouveauSolde)
        {
            var compte = await _context.Comptes.FirstOrDefaultAsync(c => c.IdCompte == idCompte);
            if (compte == null) return false;

            compte.Solde = nouveauSolde;
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<IEnumerable<CompteAvecStatut>> GetAllComptesAvecStatutAsync()
        {
            var comptes = await _context.Comptes
                .Include(c => c.HistoriquesStatut)
                    .ThenInclude(h => h.TypeStatutCompte)
                .ToListAsync();

            var comptesAvecStatut = comptes.Select(compte =>
            {
                var dernierStatut = compte.HistoriquesStatut
                    .OrderByDescending(h => h.DateChangement)
                    .FirstOrDefault();

                return new CompteAvecStatut
                {
                    IdNum = compte.IdNum,
                    IdCompte = compte.IdCompte,
                    DateOuverture = compte.DateOuverture,
                    IdClient = compte.IdClient,
                    Solde = compte.Solde,
                    StatutActuel = dernierStatut?.TypeStatutCompte?.Libelle ?? "Actif",
                    DateChangementStatut = dernierStatut?.DateChangement,
                    EstActif = dernierStatut?.TypeStatutCompte?.Libelle == "Actif" || dernierStatut == null
                };
            }).ToList();

            return comptesAvecStatut;
        }

        public async Task<CompteAvecStatut?> GetCompteAvecStatutByIdAsync(string id)
        {
            var compte = await _context.Comptes
                .Include(c => c.HistoriquesStatut)
                    .ThenInclude(h => h.TypeStatutCompte)
                .FirstOrDefaultAsync(c => c.IdCompte == id);

            if (compte == null) return null;

            var dernierStatut = compte.HistoriquesStatut
                .OrderByDescending(h => h.DateChangement)
                .FirstOrDefault();

            return new CompteAvecStatut
            {
                IdNum = compte.IdNum,
                IdCompte = compte.IdCompte,
                DateOuverture = compte.DateOuverture,
                IdClient = compte.IdClient,
                Solde = compte.Solde,
                StatutActuel = dernierStatut?.TypeStatutCompte?.Libelle ?? "Actif",
                DateChangementStatut = dernierStatut?.DateChangement,
                EstActif = dernierStatut?.TypeStatutCompte?.Libelle == "Actif" || dernierStatut == null
            };
        }
    }
}