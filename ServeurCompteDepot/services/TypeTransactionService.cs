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
    }
}