using ServeurCompteDepot.Models;
using ServeurCompteDepot.Services;
using Microsoft.AspNetCore.Mvc;

namespace ServeurCompteDepot.Controllers
{
    [ApiController]
    [Route("api/transaction")]
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

        [HttpGet("compte/{compteId}/type/{typeId}")]
        public async Task<ActionResult<IEnumerable<Transaction>>> GetTransactionsByCompteAndType(string compteId, int typeId)
        {
            try
            {
                var transactions = await _transactionService.GetTransactionsByCompteAndTypeAsync(compteId, typeId);
                return Ok(transactions);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpPost]
        public async Task<ActionResult<Transaction>> CreateTransaction([FromBody] Transaction transaction)
        {
            try
            {
                transaction.DateTransaction = DateTime.UtcNow;
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

        [HttpPost("transfert/{compteEnvoyeur}/{compteReceveur}/{montant}")]
        public async Task<ActionResult<Transfert>> CreateTransfert(string compteEnvoyeur, string compteReceveur, decimal montant)
        {
            try
            {
                var transfert = await _transactionService.CreateTransfertAsync(compteEnvoyeur, compteReceveur, montant);
                return Ok(transfert);
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
                return StatusCode(500, $"Erreur lors du transfert: {ex.Message}");
            }
        }
    }
}