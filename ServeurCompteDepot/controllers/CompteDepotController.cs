using ServeurCompteDepot.Models;
using ServeurCompteDepot.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace ServeurCompteDepot.Controllers
{
    [ApiController]
    [Route("api/CompteDepot")]
    public class CompteDepotController : ControllerBase
    {
        private readonly ICompteService _compteService;
        private readonly IHistoriqueSoldeService _historiqueSoldeService;

        public CompteDepotController(
            ICompteService compteService, 
            IHistoriqueSoldeService historiqueSoldeService)
        {
            _compteService = compteService;
            _historiqueSoldeService = historiqueSoldeService;
        }

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

        [HttpPost("by-client")]
        public async Task<ActionResult<IEnumerable<Compte>>> GetComptesByClientPost([FromBody] ClientRequest request)
        {
            try
            {
                var comptes = await _compteService.GetComptesByClientAsync(request.ClientId);
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

        [HttpPost("historique-solde")]
        public async Task<ActionResult<IEnumerable<HistoriqueSolde>>> GetHistoriqueSoldeByComptePost([FromBody] CompteRequest request)
        {
            try
            {
                var historique = await _historiqueSoldeService.GetHistoriquesSoldeByCompteAsync(request.CompteId);
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

        [HttpPost("solde")]
        public async Task<ActionResult<decimal>> GetSoldeComptePost([FromBody] CompteRequest request)
        {
            try
            {
                var solde = await _compteService.GetSoldeAsync(request.CompteId);
                return Ok(solde);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Erreur serveur: {ex.Message}");
            }
        }
    }
}