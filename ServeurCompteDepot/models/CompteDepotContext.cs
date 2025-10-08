using Microsoft.EntityFrameworkCore;
using ServeurCompteDepot.Models;

namespace ServeurCompteDepot.Models
{
    public class CompteDepotContext : DbContext
    {
        public CompteDepotContext(DbContextOptions<CompteDepotContext> options) : base(options) { }

        // DbSets pour toutes les tables
        public DbSet<Compte> Comptes { get; set; }
        public DbSet<TypeTransaction> TypesTransaction { get; set; }
        public DbSet<Transfert> Transferts { get; set; }
        public DbSet<Transaction> Transactions { get; set; }
        public DbSet<HistoriqueSolde> HistoriquesSolde { get; set; }
        public DbSet<TypeStatutCompte> TypesStatutCompte { get; set; }
        public DbSet<HistoriqueStatutCompte> HistoriquesStatutCompte { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // Configuration des relations pour Transfert
            modelBuilder.Entity<Transfert>()
                .HasOne(t => t.CompteEnvoyeur)
                .WithMany(c => c.TransfertsEnvoyes)
                .HasForeignKey(t => t.Envoyer)
                .OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<Transfert>()
                .HasOne(t => t.CompteReceveur)
                .WithMany(c => c.TransfertsRecus)
                .HasForeignKey(t => t.Receveur)
                .OnDelete(DeleteBehavior.Restrict);

            // Configuration des contraintes de précision pour les décimales
            modelBuilder.Entity<Compte>()
                .Property(c => c.Solde)
                .HasPrecision(12, 2);

            modelBuilder.Entity<Transfert>()
                .Property(t => t.Montant)
                .HasPrecision(12, 2);

            modelBuilder.Entity<Transaction>()
                .Property(t => t.Montant)
                .HasPrecision(12, 2);

            modelBuilder.Entity<HistoriqueSolde>()
                .Property(h => h.Montant)
                .HasPrecision(12, 2);
        }
    }
}
