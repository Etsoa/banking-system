using ServeurCompteDepot.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace ServeurCompteDepot.controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class CompteDepotController : ControllerBase
    {
        private readonly CompteDepotContext _context;
        public CompteDepotController(CompteDepotContext context)
        {
            _context = context;
        }

        [HttpGet]

        public async Task<ActionResult<IEnumerable<Compte>>> GetAll()
        {
            return await _context.Comptes.ToListAsync();
        }

        [HttpPost]

        public async Task<ActionResult<Compte>> Create(Compte compte)
        {
            _context.Comptes.Add(compte);
            await _context.SaveChangesAsync();
            return CreatedAtAction(nameof(GetAll), new { id = compte.IdCompte }, compte);
        }

        [HttpGet("{id}")]

        public async Task<ActionResult<Compte>> GetById(int id)
        {
            var compte = await _context.Comptes.FindAsync(id);
            if (compte == null)
                return NotFound();
            return compte;
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

        [HttpGet("test")]
        public IActionResult Test()
        {
            return Ok("API CompteDepot OK");
        }
    }
}
