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
        Task<IEnumerable<Transaction>> GetTransactionsByDateAsync(DateTime dateDebut, DateTime dateFin);
        Task<Transaction> CreateTransactionAsync(Transaction transaction);
        Task<Transfert> CreateTransfertAsync(string compteEnvoyeur, string compteReceveur, decimal montant);
        Task<Transaction?> UpdateTransactionAsync(int id, Transaction transaction);
        Task<bool> DeleteTransactionAsync(int id);
        Task<decimal> GetTotalTransactionsByCompteAsync(string idCompte, int idTypeTransaction);
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

        public async Task<IEnumerable<Transaction>> GetTransactionsByCompteAsync(string idCompte)
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

        public async Task<decimal> GetTotalTransactionsByCompteAsync(string idCompte, int idTypeTransaction)
        {
            return await _context.Transactions
                .Where(t => t.IdCompte == idCompte && t.IdTypeTransaction == idTypeTransaction)
                .SumAsync(t => t.Montant);
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

        public async Task<Transfert> CreateTransfertAsync(string compteEnvoyeur, string compteReceveur, decimal montant)
        {
            using var transaction = await _context.Database.BeginTransactionAsync();
            
            try
            {
                // Vérifier que les comptes existent
                var compteEnv = await _context.Comptes.FirstOrDefaultAsync(c => c.IdCompte == compteEnvoyeur);
                var compteRec = await _context.Comptes.FirstOrDefaultAsync(c => c.IdCompte == compteReceveur);
                
                if (compteEnv == null)
                    throw new ArgumentException($"Le compte envoyeur {compteEnvoyeur} n'existe pas");
                
                if (compteRec == null)
                    throw new ArgumentException($"Le compte receveur {compteReceveur} n'existe pas");
                
                // Vérifier que le solde est suffisant (pas de découvert autorisé pour les comptes dépôt)
                if (compteEnv.Solde < montant)
                    throw new InvalidOperationException($"Solde insuffisant. Solde actuel: {compteEnv.Solde}, Montant demandé: {montant}");
                
                // Créer la transaction sortante (débit)
                var transactionSortante = new Transaction
                {
                    IdCompte = compteEnvoyeur,
                    IdTypeTransaction = 4, // Virement sortant
                    Montant = montant,
                    DateTransaction = DateTime.UtcNow
                };
                
                _context.Transactions.Add(transactionSortante);
                await _context.SaveChangesAsync();
                
                // Créer la transaction entrante (crédit)
                var transactionEntrante = new Transaction
                {
                    IdCompte = compteReceveur,
                    IdTypeTransaction = 3, // Virement entrant
                    Montant = montant,
                    DateTransaction = DateTime.UtcNow
                };
                
                _context.Transactions.Add(transactionEntrante);
                await _context.SaveChangesAsync();
                
                // Créer le transfert
                var transfert = new Transfert
                {
                    DateTransfert = DateTime.UtcNow.Date,
                    IdTransactionEnvoyeur = transactionSortante.IdTransaction.ToString(),
                    IdTransactionReceveur = transactionEntrante.IdTransaction.ToString(),
                    Montant = montant,
                    Envoyer = compteEnvoyeur,
                    Receveur = compteReceveur
                };
                
                _context.Transferts.Add(transfert);
                await _context.SaveChangesAsync();
                
                // Mettre à jour les soldes
                compteEnv.Solde -= montant;
                compteRec.Solde += montant;
                
                // Créer les historiques de solde
                var histoSoldeEnvoyeur = new HistoriqueSolde
                {
                    IdCompte = compteEnvoyeur,
                    IdTransaction = transactionSortante.IdTransaction,
                    Montant = compteEnv.Solde,
                    DateChangement = DateTime.UtcNow
                };
                
                var histoSoldeReceveur = new HistoriqueSolde
                {
                    IdCompte = compteReceveur,
                    IdTransaction = transactionEntrante.IdTransaction,
                    Montant = compteRec.Solde,
                    DateChangement = DateTime.UtcNow
                };
                
                _context.HistoriquesSolde.Add(histoSoldeEnvoyeur);
                _context.HistoriquesSolde.Add(histoSoldeReceveur);
                
                await _context.SaveChangesAsync();
                await transaction.CommitAsync();
                
                return transfert;
            }
            catch
            {
                await transaction.RollbackAsync();
                throw;
            }
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
                if (compte == null)
                    throw new ArgumentException($"Le compte {transaction.IdCompte} n'existe pas");
                
                // Récupérer le type de transaction
                var typeTransaction = await _context.TypesTransaction.FindAsync(transaction.IdTypeTransaction);
                if (typeTransaction == null)
                    throw new ArgumentException($"Le type de transaction {transaction.IdTypeTransaction} n'existe pas");
                
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