using Microsoft.AspNetCore.Mvc;
using ServeurCompteDepot.Models;
using ServeurCompteDepot.Services;

namespace ServeurCompteDepot.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class FraisController : ControllerBase
    {
        private readonly IFraisService _fraisService;
        private readonly ILogger<FraisController> _logger;

        public FraisController(IFraisService fraisService, ILogger<FraisController> logger)
        {
            _fraisService = fraisService;
            _logger = logger;
        }

        /// <summary>
        /// Récupère tous les frais
        /// </summary>
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Frais>>> GetAllFrais()
        {
            try
            {
                var frais = await _fraisService.GetAllFraisAsync();
                return Ok(frais);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur lors de la récupération de tous les frais");
                return StatusCode(500, "Erreur interne du serveur");
            }
        }

        /// <summary>
        /// Récupère un frais par ID
        /// </summary>
        [HttpGet("{id}")]
        public async Task<ActionResult<Frais>> GetFraisById(int id)
        {
            try
            {
                var frais = await _fraisService.GetFraisByIdAsync(id);
                if (frais == null)
                {
                    return NotFound($"Frais avec l'ID {id} introuvable");
                }
                return Ok(frais);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur lors de la récupération du frais {Id}", id);
                return StatusCode(500, "Erreur interne du serveur");
            }
        }

        /// <summary>
        /// Récupère les frais par nom
        /// </summary>
        [HttpGet("nom/{nom}")]
        public async Task<ActionResult<IEnumerable<Frais>>> GetFraisByNom(string nom)
        {
            try
            {
                var frais = await _fraisService.GetFraisByNomAsync(nom);
                return Ok(frais);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur lors de la récupération des frais par nom {Nom}", nom);
                return StatusCode(500, "Erreur interne du serveur");
            }
        }

        /// <summary>
        /// Trouve les frais actuels pour un type de transaction et un montant
        /// </summary>
        [HttpGet("current")]
        public async Task<ActionResult<Frais>> FindCurrentFrais(
            [FromQuery] string typeTransaction, 
            [FromQuery] decimal montant, 
            [FromQuery] DateTime? dateReference = null)
        {
            try
            {
                var dateRef = dateReference ?? DateTime.UtcNow; // Utiliser UTC au lieu de Now
                var frais = await _fraisService.FindCurrentFraisAsync(typeTransaction, montant, dateRef);
                
                if (frais == null)
                {
                    return NotFound($"Aucun frais trouvé pour le type {typeTransaction} et le montant {montant}");
                }
                
                return Ok(frais);
            }
            catch (ArgumentException ex)
            {
                _logger.LogWarning(ex, "Paramètres invalides pour la recherche de frais");
                return BadRequest(ex.Message);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur lors de la recherche de frais actuels");
                return StatusCode(500, "Erreur interne du serveur");
            }
        }

        /// <summary>
        /// Crée un nouveau frais
        /// </summary>
        [HttpPost]
        public async Task<ActionResult<Frais>> CreateFrais([FromBody] Frais frais)
        {
            try
            {
                var nouveauFrais = await _fraisService.CreateFraisAsync(frais);
                return CreatedAtAction(nameof(GetFraisById), new { id = nouveauFrais.Id }, nouveauFrais);
            }
            catch (ArgumentException ex)
            {
                _logger.LogWarning(ex, "Données invalides pour la création de frais");
                return BadRequest(ex.Message);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur lors de la création du frais");
                return StatusCode(500, "Erreur interne du serveur");
            }
        }

        /// <summary>
        /// Met à jour un frais existant
        /// </summary>
        [HttpPut("{id}")]
        public async Task<ActionResult<Frais>> UpdateFrais(int id, [FromBody] Frais frais)
        {
            try
            {
                var fraisMisAJour = await _fraisService.UpdateFraisAsync(id, frais);
                if (fraisMisAJour == null)
                {
                    return NotFound($"Frais avec l'ID {id} introuvable");
                }
                return Ok(fraisMisAJour);
            }
            catch (ArgumentException ex)
            {
                _logger.LogWarning(ex, "Données invalides pour la mise à jour de frais");
                return BadRequest(ex.Message);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur lors de la mise à jour du frais {Id}", id);
                return StatusCode(500, "Erreur interne du serveur");
            }
        }

        /// <summary>
        /// Supprime un frais
        /// </summary>
        [HttpDelete("{id}")]
        public async Task<ActionResult> DeleteFrais(int id)
        {
            try
            {
                var supprime = await _fraisService.DeleteFraisAsync(id);
                if (!supprime)
                {
                    return NotFound($"Frais avec l'ID {id} introuvable");
                }
                return NoContent();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erreur lors de la suppression du frais {Id}", id);
                return StatusCode(500, "Erreur interne du serveur");
            }
        }
    }
}