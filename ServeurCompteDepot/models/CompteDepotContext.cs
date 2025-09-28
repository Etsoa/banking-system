using Microsoft.EntityFrameworkCore;

namespace ServeurCompteDepot.models
{
    public class CompteDepotContext : DbContext
    {
        public CompteDepotContext(DbContextOptions<CompteDepotContext> options) : base(options) { }

        public DbSet<CompteDepot> ComptesDepot { get; set; }
    }

    public class CompteDepot
    {
        public int Id { get; set; }
        public string Titulaire { get; set; }
        public decimal Solde { get; set; }
    }
}
