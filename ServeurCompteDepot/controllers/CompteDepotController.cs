using ServeurCompteDepot.models;
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
        public async Task<ActionResult<IEnumerable<CompteDepot>>> GetAll()
        {
            return await _context.ComptesDepot.ToListAsync();
        }

        [HttpPost]
        public async Task<ActionResult<CompteDepot>> Create(CompteDepot compte)
        {
            _context.ComptesDepot.Add(compte);
            await _context.SaveChangesAsync();
            return CreatedAtAction(nameof(GetAll), new { id = compte.Id }, compte);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<CompteDepot>> GetById(int id)
        {
            var compte = await _context.ComptesDepot.FindAsync(id);
            if (compte == null)
                return NotFound();
            return compte;
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var compte = await _context.ComptesDepot.FindAsync(id);
            if (compte == null)
                return NotFound();
            _context.ComptesDepot.Remove(compte);
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
