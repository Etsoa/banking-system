using ServeurCompteDepot.Models;
using Microsoft.EntityFrameworkCore;

namespace ServeurCompteDepot.Services
{
    public interface ITransactionService
    {
        Task<IEnumerable<Transaction>> GetAllTransactionsAsync();
        Task<Transaction?> GetTransactionByIdAsync(int id);
        Task<IEnumerable<Transaction>> GetTransactionsByCompteAsync(string idCompte);
        Task<IEnumerable<Transaction>> GetTransactionsByCompteAndTypeAsync(string idCompte, int idTypeTransaction);
        Task<IEnumerable<Transaction>> GetTransactionsByTypeAsync(int idTypeTransaction);
        Task<Transaction> CreateTransactionAsync(Transaction transaction);
        Task<Transaction> ExecuteTransactionAsync(Transaction transaction);
        Task<Transaction?> UpdateTransactionAsync(int id, Transaction transaction);
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
                .OrderByDescending(t => t.DateTransaction)
                .ToListAsync();
        }

        public async Task<Transaction?> GetTransactionByIdAsync(int id)
        {
            return await _context.Transactions
                .Include(t => t.TypeTransaction)
                .Include(t => t.Compte)
                .FirstOrDefaultAsync(t => t.IdTransaction == id);
        }

        public async Task<IEnumerable<Transaction>> GetTransactionsByCompteAsync(string idCompte)
        {
            return await _context.Transactions
                .Include(t => t.TypeTransaction)
                .Where(t => t.IdCompte == idCompte)
                .OrderByDescending(t => t.DateTransaction)
                .ToListAsync();
        }

        public async Task<IEnumerable<Transaction>> GetTransactionsByTypeAsync(int idTypeTransaction)
        {
            return await _context.Transactions
                .Include(t => t.Compte)
                .Where(t => t.IdTypeTransaction == idTypeTransaction)
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

        public async Task<IEnumerable<Transaction>> GetTransactionsByCompteAndTypeAsync(string idCompte, int idTypeTransaction)
        {
            return await _context.Transactions
                .Include(t => t.TypeTransaction)
                .Include(t => t.Compte)
                .Where(t => t.IdCompte == idCompte && t.IdTypeTransaction == idTypeTransaction)
                .OrderByDescending(t => t.DateTransaction)
                .ToListAsync();
        }

        /// <summary>
        /// Exécute une transaction et met à jour le solde du compte
        /// Règles métier:
        /// - Dépôt: Montant positif ajouté au solde
        /// - Retrait: Montant positif retiré du solde, SANS découvert autorisé
        /// </summary>
        public async Task<Transaction> ExecuteTransactionAsync(Transaction transaction)
        {
            using var dbTransaction = await _context.Database.BeginTransactionAsync();
            
            try
            {
                // Vérifier que le compte existe
                var compte = await _context.Comptes.FirstOrDefaultAsync(c => c.IdCompte == transaction.IdCompte);
                
                // Récupérer le type de transaction
                var typeTransaction = await _context.TypesTransaction.FindAsync(transaction.IdTypeTransaction);
                
                decimal nouveauSolde = compte.Solde;
                
                // Appliquer la règle métier selon le signe du type de transaction
                if (typeTransaction.Signe == "+")
                {
                    nouveauSolde += transaction.Montant;
                }
                else if (typeTransaction.Signe == "-")
                {
                    // Vérifier que le solde est suffisant (PAS de découvert autorisé pour les comptes dépôt)
                    if (compte.Solde < transaction.Montant)
                        throw new InvalidOperationException($"Solde insuffisant. Solde actuel: {compte.Solde}, Montant demandé: {transaction.Montant}");
                    
                    nouveauSolde -= transaction.Montant;
                }
                
                // Sauvegarder la transaction
                _context.Transactions.Add(transaction);
                await _context.SaveChangesAsync();
                
                // Mettre à jour le solde du compte
                compte.Solde = nouveauSolde;
                
                // Créer l'historique du solde
                var historiqueSolde = new HistoriqueSolde
                {
                    IdCompte = transaction.IdCompte,
                    IdTransaction = transaction.IdTransaction,
                    Montant = nouveauSolde,
                    DateChangement = DateTime.UtcNow
                };
                
                _context.HistoriquesSolde.Add(historiqueSolde);
                
                await _context.SaveChangesAsync();
                await dbTransaction.CommitAsync();
                
                return transaction;
            }
            catch
            {
                await dbTransaction.RollbackAsync();
                throw;
            }
        }
    }
}