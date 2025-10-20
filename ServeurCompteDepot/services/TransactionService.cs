using ServeurCompteDepot.Models;
using Microsoft.EntityFrameworkCore;

namespace ServeurCompteDepot.Services
{
    public interface ITransactionService
    {
        Task<IEnumerable<Transaction>> GetAllTransactionsAsync();
        Task<Transaction?> GetTransactionByIdAsync(int id);
        Task<IEnumerable<Transaction>> GetTransactionsByCompteAsync(string idCompte);
        Task<IEnumerable<TransactionAvecFrais>> GetTransactionsByCompteAvecFraisAsync(string idCompte);
        Task<IEnumerable<Transaction>> GetTransactionsByCompteAndTypeAsync(string idCompte, int idTypeTransaction);
        Task<IEnumerable<Transaction>> GetTransactionsByTypeAsync(int idTypeTransaction);
        Task<Transaction> CreateTransactionAsync(Transaction transaction);
        Task<Transaction> ExecuteTransactionAsync(Transaction transaction);
        Task<Transaction?> UpdateTransactionAsync(int id, Transaction transaction);
    }

    public class TransactionService : ITransactionService
    {
        private readonly CompteDepotContext _context;
        private readonly IFraisService _fraisService;
        private readonly ILogger<TransactionService> _logger;

        public TransactionService(CompteDepotContext context, IFraisService fraisService, ILogger<TransactionService> logger)
        {
            _context = context;
            _fraisService = fraisService;
            _logger = logger;
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

        public async Task<IEnumerable<TransactionAvecFrais>> GetTransactionsByCompteAvecFraisAsync(string idCompte)
        {
            var transactions = await _context.Transactions
                .Include(t => t.TypeTransaction)
                .Where(t => t.IdCompte == idCompte)
                .OrderByDescending(t => t.DateTransaction)
                .ToListAsync();

            var transactionsAvecFrais = new List<TransactionAvecFrais>();
            
            foreach (var transaction in transactions)
            {
                // Test: vérifier si TypeTransaction est chargé
                Console.WriteLine($"Transaction {transaction.IdTransaction}, TypeTransaction chargé: {transaction.TypeTransaction != null}, IdTypeTransaction: {transaction.IdTypeTransaction}");
                
                var transactionAvecFrais = await EnrichirTransactionAvecFraisAsync(transaction);
                transactionsAvecFrais.Add(transactionAvecFrais);
            }

            return transactionsAvecFrais;
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
        /// - Frais automatiques pour retraits et virements sortants
        /// </summary>
        public async Task<Transaction> ExecuteTransactionAsync(Transaction transaction)
        {
            using var dbTransaction = await _context.Database.BeginTransactionAsync();
            
            try
            {
                // 1. Vérifier que le compte existe
                var compte = await _context.Comptes.FirstOrDefaultAsync(c => c.IdCompte == transaction.IdCompte);
                if (compte == null)
                {
                    throw new InvalidOperationException($"Compte {transaction.IdCompte} introuvable");
                }
                
                // 2. Récupérer le type de transaction
                var typeTransaction = await _context.TypesTransaction.FindAsync(transaction.IdTypeTransaction);
                if (typeTransaction == null)
                {
                    throw new InvalidOperationException($"Type de transaction {transaction.IdTypeTransaction} introuvable");
                }
                
                // 3. Calculer le nouveau solde
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
                
                // 4. Sauvegarder la transaction
                _context.Transactions.Add(transaction);
                await _context.SaveChangesAsync();
                
                // 5. Appliquer les frais pour les retraits et virements sortants
                decimal soldeApresTransactionEtFrais = await AppliquerFraisAsync(compte, typeTransaction, transaction, nouveauSolde);
                
                // 6. Mettre à jour le solde du compte
                compte.Solde = soldeApresTransactionEtFrais;
                
                // 7. Créer l'historique du solde
                var historiqueSolde = new HistoriqueSolde
                {
                    IdCompte = transaction.IdCompte,
                    IdTransaction = transaction.IdTransaction,
                    Montant = soldeApresTransactionEtFrais,
                    DateChangement = transaction.DateTransaction
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

        /// <summary>
        /// Applique les frais pour les retraits et virements sortants
        /// </summary>
        private async Task<decimal> AppliquerFraisAsync(Compte compte, TypeTransaction typeTransaction, Transaction transaction, decimal nouveauSolde)
        {
            // Les frais s'appliquent uniquement aux retraits et virements sortants
            if (!typeTransaction.Libelle.Equals("Retrait", StringComparison.OrdinalIgnoreCase) &&
                !typeTransaction.Libelle.Equals("Virement sortant", StringComparison.OrdinalIgnoreCase))
            {
                return nouveauSolde;
            }

            try
            {
                var fraisApplicables = await _fraisService.FindCurrentFraisAsync(
                    typeTransaction.Libelle, 
                    transaction.Montant, 
                    transaction.DateTransaction);

                if (fraisApplicables == null)
                {
                    return nouveauSolde; // Pas de frais applicables
                }

                decimal montantFrais = fraisApplicables.Valeur;
                decimal soldeApresTransactionEtFrais = nouveauSolde - montantFrais;

                // Vérifier que l'application des frais ne rend pas le solde négatif
                // (PAS de découvert autorisé pour les comptes dépôt)
                if (soldeApresTransactionEtFrais < 0)
                {
                    throw new InvalidOperationException(
                        $"Solde insuffisant pour couvrir les frais de {montantFrais}. " +
                        $"Solde après transaction: {nouveauSolde}, " +
                        $"Solde après frais: {soldeApresTransactionEtFrais}");
                }

                _logger.LogInformation("Frais de {MontantFrais} appliqués pour {TypeTransaction} de {Montant}", 
                    montantFrais, typeTransaction.Libelle, transaction.Montant);

                return soldeApresTransactionEtFrais;
            }
            catch (Exception ex)
            {
                _logger.LogWarning(ex, "Erreur lors de la récupération des frais pour {TypeTransaction}", typeTransaction.Libelle);
                return nouveauSolde; // Continue sans frais en cas d'erreur
            }
        }

        /// <summary>
        /// Enrichit une transaction avec les informations de frais
        /// </summary>
        private async Task<TransactionAvecFrais> EnrichirTransactionAvecFraisAsync(Transaction transaction)
        {
            try
            {
                // Le TypeTransaction devrait déjà être chargé via Include
                var typeTransaction = transaction.TypeTransaction;

                _logger.LogInformation($"Enrichissement transaction {transaction.IdTransaction}, Type chargé: {typeTransaction != null}, Libelle: {typeTransaction?.Libelle}");

                // Si le TypeTransaction n'est pas chargé, essayons de le récupérer explicitement
                if (typeTransaction == null)
                {
                    _logger.LogWarning($"TypeTransaction non chargé pour transaction {transaction.IdTransaction}, récupération explicite...");
                    typeTransaction = await _context.TypesTransaction.FindAsync(transaction.IdTypeTransaction);
                    _logger.LogInformation($"TypeTransaction récupéré: {typeTransaction?.Libelle}");
                }

                // Vérifier si des frais s'appliquent à ce type de transaction
                if (typeTransaction != null &&
                    (typeTransaction.Libelle.Equals("Retrait", StringComparison.OrdinalIgnoreCase) ||
                     typeTransaction.Libelle.Equals("Virement sortant", StringComparison.OrdinalIgnoreCase)))
                {
                    _logger.LogInformation($"Type de transaction {typeTransaction.Libelle} applicable pour frais, montant: {transaction.Montant}");
                    
                    // Récupérer les frais applicables
                    var dateReferenceUtc = transaction.DateTransaction.Kind == DateTimeKind.Utc 
                        ? transaction.DateTransaction 
                        : DateTime.SpecifyKind(transaction.DateTransaction, DateTimeKind.Utc);
                    
                    var fraisApplicables = await _fraisService.FindCurrentFraisAsync(
                        typeTransaction.Libelle,
                        transaction.Montant,
                        dateReferenceUtc);

                    _logger.LogInformation($"Frais trouvés: {fraisApplicables?.Nom} - {fraisApplicables?.Valeur}");

                    return new TransactionAvecFrais(transaction, typeTransaction, fraisApplicables);
                }
                else
                {
                    _logger.LogInformation($"Pas de frais pour le type: {typeTransaction?.Libelle}");
                    return new TransactionAvecFrais(transaction, typeTransaction);
                }
            }
            catch (Exception ex)
            {
                _logger.LogWarning(ex, "Erreur lors de l'enrichissement de la transaction {TransactionId}", transaction.IdTransaction);
                // En cas d'erreur, retourner la transaction sans frais
                return new TransactionAvecFrais(transaction);
            }
        }
    }
}