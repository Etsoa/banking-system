using ServeurCompteDepot.Models;
using ServeurCompteDepot.models;
using ServeurCompteDepot.Services;
using Microsoft.AspNetCore.Mvc;

namespace ServeurCompteDepot.Controllers
{
    [ApiController]
    [Route("api/CompteDepot/transfert")]
    public class TransfertController : ControllerBase
    {
        private readonly ITransfertService _transfertService;
        private readonly CompteDepotContext _context;

        public TransfertController(ITransfertService transfertService, CompteDepotContext context)
        {
            _transfertService = transfertService;
            _context = context;
        }

        [HttpGet("avec-frais")]
        public async Task<ActionResult<IEnumerable<TransfertAvecFrais>>> GetAllTransfertsAvecFrais()
        {
            try
            {
                var transferts = await _transfertService.GetAllTransfertsAvecFraisAsync();
                return Ok(transferts);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }

        [HttpGet("compte/{compteId}/avec-frais")]
        public async Task<ActionResult<IEnumerable<TransfertAvecFrais>>> GetTransfertsByCompteAvecFrais(string compteId)
        {
            try
            {
                var transferts = await _transfertService.GetTransfertsByCompteAvecFraisAsync(compteId);
                return Ok(transferts);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
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
                // Log des données reçues pour debug
                Console.WriteLine($"Transfert request reçu: Envoyeur={request.CompteEnvoyeur}, Receveur={request.CompteReceveur}, Montant={request.Montant}, Date={request.DateTransfert}");
                
                var transfert = await _transfertService.CreateTransfertAsync(
                    request.CompteEnvoyeur, 
                    request.CompteReceveur, 
                    request.Montant,
                    request.DateTransfert);
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
                Console.WriteLine($"Erreur détaillée: {ex}");
                return StatusCode(500, $"Erreur lors du transfert: {ex.Message}");
            }
        }

        [HttpPost("inter-systeme")]
        public async Task<ActionResult<Transfert>> CreateTransfertInterSysteme([FromBody] Transfert transfert)
        {
            try
            {
                // Créer le transfert directement sans validation des comptes
                var nouveauTransfert = new Transfert
                {
                    DateTransfert = transfert.DateTransfert,
                    IdTransactionEnvoyeur = transfert.IdTransactionEnvoyeur,
                    IdTransactionReceveur = transfert.IdTransactionReceveur,
                    Montant = transfert.Montant,
                    Envoyer = transfert.Envoyer,
                    Receveur = transfert.Receveur
                };

                // Sauvegarder directement sans passer par le service complet (pas de création de transactions)
                _context.Transferts.Add(nouveauTransfert);
                await _context.SaveChangesAsync();

                return CreatedAtAction(nameof(GetTransfertById), 
                    new { id = nouveauTransfert.IdTransfert }, nouveauTransfert);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur lors du transfert inter-système: {ex.Message}");
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