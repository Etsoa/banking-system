using Microsoft.AspNetCore.Mvc;
using ServeurCompteDepot.Models;
using System;
using System.Threading.Tasks;

namespace ServeurCompteDepot.controllers
{
    [ApiController]
    [Route("[controller]")]
    public class TestConnexionController : ControllerBase
    {
        private readonly CompteDepotContext _context;

        public TestConnexionController(CompteDepotContext context)
        {
            _context = context;
        }

        [HttpGet("test-connexion-db")]
        public async Task<IActionResult> TestConnexion()
        {
            try
            {
                // On force une requête simple pour tester la connexion
                var _ = _context.Comptes.FirstOrDefault();
                return Ok("Connexion à la base de données réussie !");
            }
            catch (Exception ex)
            {
                return BadRequest("Erreur lors de la connexion : " + ex.Message);
            }
        }
    }
}
