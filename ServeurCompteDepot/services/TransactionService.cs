using ServeurCompteDepot.Models;
using Microsoft.EntityFrameworkCore;

namespace ServeurCompteDepot.Services
{
    public interface ITransactionService
    {
        Task<IEnumerable<Transaction>> GetAllTransactionsAsync();
        Task<Transaction?> GetTransactionByIdAsync(int id);
        Task<IEnumerable<Transaction>> GetTransactionsByCompteAsync(int idCompte);
        Task<IEnumerable<Transaction>> GetTransactionsByTypeAsync(int idTypeTransaction);
        Task<IEnumerable<Transaction>> GetTransactionsByDateAsync(DateTime dateDebut, DateTime dateFin);
        Task<Transaction> CreateTransactionAsync(Transaction transaction);
        Task<Transaction?> UpdateTransactionAsync(int id, Transaction transaction);
        Task<bool> DeleteTransactionAsync(int id);
        Task<decimal> GetTotalTransactionsByCompteAsync(int idCompte, int idTypeTransaction);
    }

    public class TransactionService : ITransactionService
    {
        private readonly CompteDepotContext _context;

        public TransactionService(CompteDepotContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<Transaction>> GetAllTransactionsAsync()
        {
            return await _context.Transactions
                .Include(t => t.TypeTransaction)
                .Include(t => t.Compte)
                .Include(t => t.Transfert)
                .OrderByDescending(t => t.DateTransaction)
                .ToListAsync();
        }

        public async Task<Transaction?> GetTransactionByIdAsync(int id)
        {
            return await _context.Transactions
                .Include(t => t.TypeTransaction)
                .Include(t => t.Compte)
                .Include(t => t.Transfert)
                .FirstOrDefaultAsync(t => t.IdTransaction == id);
        }

        public async Task<IEnumerable<Transaction>> GetTransactionsByCompteAsync(int idCompte)
        {
            return await _context.Transactions
                .Include(t => t.TypeTransaction)
                .Include(t => t.Transfert)
                .Where(t => t.IdCompte == idCompte)
                .OrderByDescending(t => t.DateTransaction)
                .ToListAsync();
        }

        public async Task<IEnumerable<Transaction>> GetTransactionsByTypeAsync(int idTypeTransaction)
        {
            return await _context.Transactions
                .Include(t => t.Compte)
                .Include(t => t.Transfert)
                .Where(t => t.IdTypeTransaction == idTypeTransaction)
                .OrderByDescending(t => t.DateTransaction)
                .ToListAsync();
        }

        public async Task<IEnumerable<Transaction>> GetTransactionsByDateAsync(DateTime dateDebut, DateTime dateFin)
        {
            return await _context.Transactions
                .Include(t => t.TypeTransaction)
                .Include(t => t.Compte)
                .Where(t => t.DateTransaction >= dateDebut && t.DateTransaction <= dateFin)
                .OrderByDescending(t => t.DateTransaction)
                .ToListAsync();
        }

        public async Task<Transaction> CreateTransactionAsync(Transaction transaction)
        {
            _context.Transactions.Add(transaction);
            await _context.SaveChangesAsync();
            return transaction;
        }

        public async Task<Transaction?> UpdateTransactionAsync(int id, Transaction transaction)
        {
            var existingTransaction = await _context.Transactions.FindAsync(id);
            if (existingTransaction == null) return null;

            existingTransaction.Montant = transaction.Montant;
            existingTransaction.IdTypeTransaction = transaction.IdTypeTransaction;
            existingTransaction.IdCompte = transaction.IdCompte;

            await _context.SaveChangesAsync();
            return existingTransaction;
        }

        public async Task<bool> DeleteTransactionAsync(int id)
        {
            var transaction = await _context.Transactions.FindAsync(id);
            if (transaction == null) return false;

            _context.Transactions.Remove(transaction);
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<decimal> GetTotalTransactionsByCompteAsync(int idCompte, int idTypeTransaction)
        {
            return await _context.Transactions
                .Where(t => t.IdCompte == idCompte && t.IdTypeTransaction == idTypeTransaction)
                .SumAsync(t => t.Montant);
        }
    }
}