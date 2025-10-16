using ServeurCompteDepot.Models;
using ServeurCompteDepot.Services;
using Microsoft.AspNetCore.Mvc;

namespace ServeurCompteDepot.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class TransfertController : ControllerBase
    {
        private readonly ITransfertService _transfertService;

        public TransfertController(ITransfertService transfertService)
        {
            _transfertService = transfertService;
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

        [HttpPost("{compteEnvoyeur}/{compteReceveur}/{montant}")]
        public async Task<ActionResult<Transfert>> CreateTransfert(string compteEnvoyeur, string compteReceveur, decimal montant)
        {
            try
            {
                var transfert = await _transfertService.CreateTransfertAsync(compteEnvoyeur, compteReceveur, montant);
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
    }
}