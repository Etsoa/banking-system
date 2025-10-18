using ServeurCompteDepot.Models;
using ServeurCompteDepot.Services;
using Microsoft.AspNetCore.Mvc;

namespace ServeurCompteDepot.Controllers
{
    [ApiController]
    [Route("api/CompteDepot/transaction")]
    public class TransactionController : ControllerBase
    {
        private readonly ITransactionService _transactionService;

        public TransactionController(ITransactionService transactionService)
        {
            _transactionService = transactionService;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Transaction>>> GetAllTransactions()
        {
            try
            {
                var transactions = await _transactionService.GetAllTransactionsAsync();
                return Ok(transactions);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<Transaction>> GetTransactionById(int id)
        {
            try
            {
                var transaction = await _transactionService.GetTransactionByIdAsync(id);
                if (transaction == null)
                    return NotFound($"Transaction {id} non trouvée");
                return Ok(transaction);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpGet("compte/{compteId}")]
        public async Task<ActionResult<IEnumerable<Transaction>>> GetTransactionsByCompte(string compteId)
        {
            try
            {
                var transactions = await _transactionService.GetTransactionsByCompteAsync(compteId);
                return Ok(transactions);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpPost("by-compte")]
        public async Task<ActionResult<IEnumerable<Transaction>>> GetTransactionsByComptePost([FromBody] CompteRequest request)
        {
            try
            {
                var transactions = await _transactionService.GetTransactionsByCompteAsync(request.CompteId);
                return Ok(transactions);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpPost("by-compte-and-type")]
        public async Task<ActionResult<IEnumerable<Transaction>>> GetTransactionsByCompteAndType([FromBody] TransactionsByCompteAndTypeRequest request)
        {
            try
            {
                var transactions = await _transactionService.GetTransactionsByCompteAndTypeAsync(request.CompteId, request.TypeId);
                return Ok(transactions);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpPost]
        public async Task<ActionResult<Transaction>> CreateTransaction([FromBody] TransactionRequest request)
        {
            try
            {
                // Créer l'objet Transaction à partir de la requête
                var transaction = new Transaction
                {
                    DateTransaction = request.DateTransaction,
                    Montant = request.Montant,
                    IdTypeTransaction = request.IdTypeTransaction,
                    IdCompte = request.IdCompte
                };

                var nouvelleTransaction = await _transactionService.ExecuteTransactionAsync(transaction);
                return CreatedAtAction(nameof(GetTransactionById), 
                    new { id = nouvelleTransaction.IdTransaction }, nouvelleTransaction);
            }
            catch (ArgumentException ex)
            {
                return BadRequest(ex.Message);
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(ex.Message);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur lors de la création de la transaction: {ex.Message}");
            }
        }
    }
}