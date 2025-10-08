using ServeurCompteDepot.models;
using ServeurCompteDepot.Models;
using Microsoft.EntityFrameworkCore;

namespace ServeurCompteDepot.Services
{
    public interface ITypeTransactionService
    {
        Task<IEnumerable<TypeTransaction>> GetAllTypesTransactionAsync();
        Task<TypeTransaction?> GetTypeTransactionByIdAsync(int id);
        Task<IEnumerable<TypeTransaction>> GetTypesTransactionActifsAsync();
        Task<TypeTransaction?> GetTypeTransactionByLibelleAsync(string libelle);
        Task<TypeTransaction> CreateTypeTransactionAsync(TypeTransaction typeTransaction);
        Task<TypeTransaction?> UpdateTypeTransactionAsync(int id, TypeTransaction typeTransaction);
        Task<bool> DeleteTypeTransactionAsync(int id);
        Task<bool> ToggleTypeTransactionAsync(int id);
    }

    public class TypeTransactionService : ITypeTransactionService
    {
        private readonly CompteDepotContext _context;

        public TypeTransactionService(CompteDepotContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<TypeTransaction>> GetAllTypesTransactionAsync()
        {
            return await _context.TypesTransaction
                .Include(tt => tt.Transactions)
                .ToListAsync();
        }

        public async Task<TypeTransaction?> GetTypeTransactionByIdAsync(int id)
        {
            return await _context.TypesTransaction
                .Include(tt => tt.Transactions)
                .FirstOrDefaultAsync(tt => tt.IdTypeTransaction == id);
        }

        public async Task<IEnumerable<TypeTransaction>> GetTypesTransactionActifsAsync()
        {
            return await _context.TypesTransaction
                .Where(tt => tt.Actif)
                .ToListAsync();
        }

        public async Task<TypeTransaction?> GetTypeTransactionByLibelleAsync(string libelle)
        {
            return await _context.TypesTransaction
                .FirstOrDefaultAsync(tt => tt.Libelle.ToLower() == libelle.ToLower());
        }

        public async Task<TypeTransaction> CreateTypeTransactionAsync(TypeTransaction typeTransaction)
        {
            _context.TypesTransaction.Add(typeTransaction);
            await _context.SaveChangesAsync();
            return typeTransaction;
        }

        public async Task<TypeTransaction?> UpdateTypeTransactionAsync(int id, TypeTransaction typeTransaction)
        {
            var existingType = await _context.TypesTransaction.FindAsync(id);
            if (existingType == null) return null;

            existingType.Libelle = typeTransaction.Libelle;
            existingType.Actif = typeTransaction.Actif;
            existingType.Signe = typeTransaction.Signe;

            await _context.SaveChangesAsync();
            return existingType;
        }

        public async Task<bool> DeleteTypeTransactionAsync(int id)
        {
            var typeTransaction = await _context.TypesTransaction.FindAsync(id);
            if (typeTransaction == null) return false;

            _context.TypesTransaction.Remove(typeTransaction);
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<bool> ToggleTypeTransactionAsync(int id)
        {
            var typeTransaction = await _context.TypesTransaction.FindAsync(id);
            if (typeTransaction == null) return false;

            typeTransaction.Actif = !typeTransaction.Actif;
            await _context.SaveChangesAsync();
            return true;
        }
    }
}