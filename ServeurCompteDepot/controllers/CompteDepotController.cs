using ServeurCompteDepot.Models;
using ServeurCompteDepot.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace ServeurCompteDepot.controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class CompteDepotController : ControllerBase
    {
        private readonly CompteDepotContext _context;
        private readonly ICompteService _compteService;

        public CompteDepotController(CompteDepotContext context, ICompteService compteService)
        {
            _context = context;
            _compteService = compteService;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Compte>>> GetAll()
        {
            return await _context.Comptes.ToListAsync();
        }

        [HttpGet("avec-statut")]
        public async Task<ActionResult<IEnumerable<CompteAvecStatut>>> GetAllAvecStatut()
        {
            var comptes = await _compteService.GetAllComptesAvecStatutAsync();
            return Ok(comptes);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<Compte>> GetById(int id)
        {
            var compte = await _context.Comptes.FindAsync(id);
            if (compte == null)
                return NotFound();
            return compte;
        }

        [HttpGet("{id}/avec-statut")]
        public async Task<ActionResult<CompteAvecStatut>> GetByIdAvecStatut(int id)
        {
            var compte = await _compteService.GetCompteAvecStatutByIdAsync(id);
            if (compte == null)
                return NotFound();
            return Ok(compte);
        }

        [HttpPost]
        public async Task<ActionResult<Compte>> Create(Compte compte)
        {
            _context.Comptes.Add(compte);
            await _context.SaveChangesAsync();
            return CreatedAtAction(nameof(GetAll), new { id = compte.IdCompte }, compte);
        }

        [HttpGet("client/{clientId}")]
        public async Task<ActionResult<IEnumerable<Compte>>> GetByClientId(int clientId)
        {
            var comptes = await _context.Comptes
                .Where(c => c.IdClient == clientId)
                .ToListAsync();
            return Ok(comptes);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var compte = await _context.Comptes.FindAsync(id);
            if (compte == null)
                return NotFound();
            _context.Comptes.Remove(compte);
            await _context.SaveChangesAsync();
            return NoContent();
        }
    }
}

        [HttpGet("test")]
        public IActionResult Test()
        {
            return Ok("API CompteDepot OK");
        }
    }
}
