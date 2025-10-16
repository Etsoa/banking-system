using ServeurCompteDepot.Models;
using ServeurCompteDepot.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace ServeurCompteDepot.Controllers
{
    [ApiController]
    [Route("api/compte-depot")]
    public class CompteDepotController : ControllerBase
    {
        private readonly ICompteService _compteService;
        private readonly ITransactionService _transactionService;
        private readonly ITransfertService _transfertService;
        private readonly IHistoriqueSoldeService _historiqueSoldeService;

        public CompteDepotController(
            ICompteService compteService, 
            ITransactionService transactionService,
            ITransfertService transfertService,
            IHistoriqueSoldeService historiqueSoldeService)
        {
            _compteService = compteService;
            _transactionService = transactionService;
            _transfertService = transfertService;
            _historiqueSoldeService = historiqueSoldeService;
        }

        // ========== ENDPOINTS COMPTES ==========
        
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Compte>>> GetAllComptes()
        {
            try
            {
                var comptes = await _compteService.GetAllComptesAsync();
                return Ok(comptes);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpGet("avec-statut")]
        public async Task<ActionResult<IEnumerable<CompteAvecStatut>>> GetAllComptesAvecStatut()
        {
            try
            {
                var comptes = await _compteService.GetAllComptesAvecStatutAsync();
                return Ok(comptes);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<Compte>> GetCompteById(string id)
        {
            try
            {
                var compte = await _compteService.GetCompteByIdAsync(id);
                if (compte == null)
                    return NotFound($"Compte {id} non trouvé");
                return Ok(compte);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpGet("{id}/avec-statut")]
        public async Task<ActionResult<CompteAvecStatut>> GetCompteAvecStatutById(string id)
        {
            try
            {
                var compte = await _compteService.GetCompteAvecStatutByIdAsync(id);
                if (compte == null)
                    return NotFound($"Compte {id} non trouvé");
                return Ok(compte);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpPost]
        public async Task<ActionResult<Compte>> CreateCompte([FromBody] Compte compte)
        {
            try
            {
                var nouveauCompte = await _compteService.CreateCompteAsync(compte);
                return CreatedAtAction(nameof(GetCompteById), new { id = nouveauCompte.IdCompte }, nouveauCompte);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur lors de la création du compte: {ex.Message}");
            }
        }

        [HttpGet("client/{clientId}")]
        public async Task<ActionResult<IEnumerable<Compte>>> GetComptesByClient(int clientId)
        {
            try
            {
                var comptes = await _compteService.GetComptesByClientAsync(clientId);
                return Ok(comptes);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteCompte(string id)
        {
            try
            {
                var success = await _compteService.DeleteCompteAsync(id);
                if (!success)
                    return NotFound($"Compte {id} non trouvé");
                return NoContent();
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur lors de la suppression: {ex.Message}");
            }
        }

        // ========== ENDPOINTS TRANSACTIONS ==========

        [HttpGet("{compteId}/transactions")]
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

        [HttpGet("{compteId}/transactions/type/{typeId}")]
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

        [HttpPost("transactions")]
        public async Task<ActionResult<Transaction>> CreateTransaction([FromBody] Transaction transaction)
        {
            try
            {
                transaction.DateTransaction = DateTime.UtcNow;
                var nouvelleTransaction = await _transactionService.ExecuteTransactionAsync(transaction);
                return CreatedAtAction(nameof(GetTransactionsByCompte), 
                    new { compteId = transaction.IdCompte }, nouvelleTransaction);
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

        // ========== ENDPOINTS TRANSFERTS ==========

        [HttpPost("transactions/transfert/{compteEnvoyeur}/{compteReceveur}/{montant}")]
        public async Task<ActionResult<Transfert>> CreateTransfert(string compteEnvoyeur, string compteReceveur, decimal montant)
        {
            try
            {
                var transfert = await _transactionService.CreateTransfertAsync(compteEnvoyeur, compteReceveur, montant);
                return CreatedAtAction(nameof(GetTransfertsByCompte), 
                    new { compteId = compteEnvoyeur }, transfert);
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

        [HttpGet("transferts")]
        public async Task<ActionResult<IEnumerable<Transfert>>> GetAllTransferts()
        {
            try
            {
                var transferts = await _transfertService.GetAllTransfertsAsync();
                return Ok(transferts);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpGet("transferts/compte/{compteId}")]
        public async Task<ActionResult<IEnumerable<Transfert>>> GetTransfertsByCompte(string compteId)
        {
            try
            {
                var transferts = await _transfertService.GetTransfertsByCompteAsync(compteId);
                return Ok(transferts);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        // ========== ENDPOINTS HISTORIQUE SOLDE ==========

        [HttpGet("{compteId}/historique-solde")]
        public async Task<ActionResult<IEnumerable<HistoriqueSolde>>> GetHistoriqueSoldeByCompte(string compteId)
        {
            try
            {
                var historique = await _historiqueSoldeService.GetHistoriquesSoldeByCompteAsync(compteId);
                return Ok(historique);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpGet("{compteId}/solde")]
        public async Task<ActionResult<decimal>> GetSoldeCompte(string compteId)
        {
            try
            {
                var solde = await _compteService.GetSoldeAsync(compteId);
                return Ok(solde);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }
    }
}