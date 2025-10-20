using ServeurCompteDepot.Models;
using Microsoft.EntityFrameworkCore;

namespace ServeurCompteDepot.Services
{
    public interface IFraisService
    {
        Task<IEnumerable<Frais>> GetAllFraisAsync();
        Task<Frais?> GetFraisByIdAsync(int id);
        Task<IEnumerable<Frais>> GetFraisByNomAsync(string nom);
        Task<Frais?> FindCurrentFraisAsync(string typeTransaction, decimal montant, DateTime dateReference);
        Task<Frais> CreateFraisAsync(Frais frais);
        Task<Frais?> UpdateFraisAsync(int id, Frais frais);
        Task<bool> DeleteFraisAsync(int id);
    }

    public class FraisService : IFraisService
    {
        private readonly CompteDepotContext _context;
        private readonly ILogger<FraisService> _logger;

        public FraisService(CompteDepotContext context, ILogger<FraisService> logger)
        {
            _context = context;
            _logger = logger;
        }

        public async Task<IEnumerable<Frais>> GetAllFraisAsync()
        {
            return await _context.Frais
                .OrderByDescending(f => f.DateDebut)
                .ToListAsync();
        }

        public async Task<Frais?> GetFraisByIdAsync(int id)
        {
            return await _context.Frais.FindAsync(id);
        }

        public async Task<IEnumerable<Frais>> GetFraisByNomAsync(string nom)
        {
            return await _context.Frais
                .Where(f => f.Nom == nom)
                .OrderByDescending(f => f.DateDebut)
                .ToListAsync();
        }

        /// <summary>
        /// Trouve les frais actuels pour un type de transaction et un montant donnés
        /// </summary>
        public async Task<Frais?> FindCurrentFraisAsync(string typeTransaction, decimal montant, DateTime dateReference)
        {
            try
            {
                if (string.IsNullOrWhiteSpace(typeTransaction))
                {
                    throw new ArgumentException("Le type de transaction est obligatoire");
                }

                if (montant <= 0)
                {
                    throw new ArgumentException("Le montant doit être positif");
                }

                // Déterminer le nom des frais selon le type de transaction
                string nomFrais;
                if (typeTransaction.Equals("Retrait", StringComparison.OrdinalIgnoreCase))
                {
                    nomFrais = "Frais de retrait";
                }
                else if (typeTransaction.Equals("Virement sortant", StringComparison.OrdinalIgnoreCase))
                {
                    nomFrais = "Frais de virement sortant";
                }
                else
                {
                    // Pour les autres types de transaction, pas de frais
                    return null;
                }

                return await _context.Frais
                    .Where(f => f.Nom == nomFrais 
                        && f.MontantMin <= montant 
                        && f.MontantMax >= montant 
                        && f.DateDebut <= dateReference)
                    .OrderByDescending(f => f.DateDebut)
                    .FirstOrDefaultAsync();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur lors de la recherche des frais actuels pour {TypeTransaction}", typeTransaction);
                throw new InvalidOperationException("Erreur lors de la recherche des frais actuels", ex);
            }
        }

        public async Task<Frais> CreateFraisAsync(Frais frais)
        {
            try
            {
                ValidateFraisData(frais);

                _context.Frais.Add(frais);
                await _context.SaveChangesAsync();
                return frais;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur lors de la création du frais");
                throw new InvalidOperationException("Erreur lors de la création du frais", ex);
            }
        }

        public async Task<Frais?> UpdateFraisAsync(int id, Frais frais)
        {
            try
            {
                var existingFrais = await GetFraisByIdAsync(id);
                if (existingFrais == null)
                {
                    return null;
                }

                ValidateFraisData(frais);

                existingFrais.DateDebut = frais.DateDebut;
                existingFrais.Nom = frais.Nom;
                existingFrais.MontantMin = frais.MontantMin;
                existingFrais.MontantMax = frais.MontantMax;
                existingFrais.Valeur = frais.Valeur;

                await _context.SaveChangesAsync();
                return existingFrais;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur lors de la mise à jour du frais {Id}", id);
                throw new InvalidOperationException("Erreur lors de la mise à jour du frais", ex);
            }
        }

        public async Task<bool> DeleteFraisAsync(int id)
        {
            try
            {
                var frais = await GetFraisByIdAsync(id);
                if (frais == null)
                {
                    return false;
                }

                _context.Frais.Remove(frais);
                await _context.SaveChangesAsync();
                return true;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur lors de la suppression du frais {Id}", id);
                throw new InvalidOperationException("Erreur lors de la suppression du frais", ex);
            }
        }

        /// <summary>
        /// Validation des données de frais
        /// </summary>
        private static void ValidateFraisData(Frais frais)
        {
            if (frais == null)
            {
                throw new ArgumentNullException(nameof(frais), "Les données du frais sont obligatoires");
            }

            if (string.IsNullOrWhiteSpace(frais.Nom))
            {
                throw new ArgumentException("Le nom du frais est obligatoire");
            }

            if (frais.MontantMin < 0)
            {
                throw new ArgumentException("Le montant minimum doit être positif ou nul");
            }

            if (frais.MontantMax < 0)
            {
                throw new ArgumentException("Le montant maximum doit être positif ou nul");
            }

            if (frais.MontantMin > frais.MontantMax)
            {
                throw new ArgumentException("Le montant minimum ne peut pas être supérieur au montant maximum");
            }

            if (frais.Valeur < 0)
            {
                throw new ArgumentException("Le montant des frais doit être positif ou nul");
            }
        }
    }
}