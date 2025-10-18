using ServeurCompteDepot.Models;
using ServeurCompteDepot.Services;
using Microsoft.AspNetCore.Mvc;

namespace ServeurCompteDepot.Controllers
{
    [ApiController]
    [Route("api/CompteDepot/transfert")]
    public class TransfertController : ControllerBase
    {
        private readonly ITransfertService _transfertService;
        private readonly ITransactionService _transactionService;

        public TransfertController(ITransfertService transfertService, ITransactionService transactionService)
        {
            _transfertService = transfertService;
            _transactionService = transactionService;
        }

        [HttpGet]
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

        [HttpGet("{id}")]
        public async Task<ActionResult<Transfert>> GetTransfertById(int id)
        {
            try
            {
                var transfert = await _transfertService.GetTransfertByIdAsync(id);
                if (transfert == null)
                    return NotFound($"Transfert {id} non trouvé");
                return Ok(transfert);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpGet("compte/{compteId}")]
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

        [HttpPost("by-compte")]
        public async Task<ActionResult<IEnumerable<Transfert>>> GetTransfertsByComptePost([FromBody] CompteRequest request)
        {
            try
            {
                var transferts = await _transfertService.GetTransfertsByCompteAsync(request.CompteId);
                return Ok(transferts);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpGet("envoyeur/{compteId}")]
        public async Task<ActionResult<IEnumerable<Transfert>>> GetTransfertsByEnvoyeur(string compteId)
        {
            try
            {
                var transferts = await _transfertService.GetTransfertsByCompteEnvoyeurAsync(compteId);
                return Ok(transferts);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpPost("by-envoyeur")]
        public async Task<ActionResult<IEnumerable<Transfert>>> GetTransfertsByEnvoyeurPost([FromBody] CompteRequest request)
        {
            try
            {
                var transferts = await _transfertService.GetTransfertsByCompteEnvoyeurAsync(request.CompteId);
                return Ok(transferts);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpGet("receveur/{compteId}")]
        public async Task<ActionResult<IEnumerable<Transfert>>> GetTransfertsByReceveur(string compteId)
        {
            try
            {
                var transferts = await _transfertService.GetTransfertsByCompteReceveurAsync(compteId);
                return Ok(transferts);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpPost("by-receveur")]
        public async Task<ActionResult<IEnumerable<Transfert>>> GetTransfertsByReceveurPost([FromBody] CompteRequest request)
        {
            try
            {
                var transferts = await _transfertService.GetTransfertsByCompteReceveurAsync(request.CompteId);
                return Ok(transferts);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpPost]
        public async Task<ActionResult<Transfert>> CreateTransfert([FromBody] TransfertRequest request)
        {
            try
            {
                var transfert = await _transactionService.CreateTransfertAsync(
                    request.CompteEnvoyeur, 
                    request.CompteReceveur, 
                    request.Montant);
                return CreatedAtAction(nameof(GetTransfertById), 
                    new { id = transfert.IdTransfert }, transfert);
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

        [HttpGet("statistiques/{compteId}")]
        public async Task<ActionResult<object>> GetStatistiquesTransferts(string compteId)
        {
            try
            {
                var totalSortant = await _transfertService.GetTotalTransfertsSortantsAsync(compteId);
                var totalEntrant = await _transfertService.GetTotalTransfertsEntrantsAsync(compteId);
                var nombreSortant = await _transfertService.GetNombreTransfertsSortantsAsync(compteId);
                var nombreEntrant = await _transfertService.GetNombreTransfertsEntrantsAsync(compteId);

                var statistiques = new
                {
                    CompteId = compteId,
                    TransfertsSortants = new
                    {
                        Nombre = nombreSortant,
                        MontantTotal = totalSortant
                    },
                    TransfertsEntrants = new
                    {
                        Nombre = nombreEntrant,
                        MontantTotal = totalEntrant
                    }
                };

                return Ok(statistiques);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpPost("statistiques")]
        public async Task<ActionResult<object>> GetStatistiquesTransfertsPost([FromBody] CompteRequest request)
        {
            try
            {
                var totalSortant = await _transfertService.GetTotalTransfertsSortantsAsync(request.CompteId);
                var totalEntrant = await _transfertService.GetTotalTransfertsEntrantsAsync(request.CompteId);
                var nombreSortant = await _transfertService.GetNombreTransfertsSortantsAsync(request.CompteId);
                var nombreEntrant = await _transfertService.GetNombreTransfertsEntrantsAsync(request.CompteId);

                var statistiques = new
                {
                    CompteId = request.CompteId,
                    TransfertsSortants = new
                    {
                        Nombre = nombreSortant,
                        MontantTotal = totalSortant
                    },
                    TransfertsEntrants = new
                    {
                        Nombre = nombreEntrant,
                        MontantTotal = totalEntrant
                    }
                };

                return Ok(statistiques);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }
    }
}