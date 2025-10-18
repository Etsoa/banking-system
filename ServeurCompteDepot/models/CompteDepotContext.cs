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
        public DbSet<Frais> Frais { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // Configuration de la clé primaire et de la colonne calculée pour Compte
            modelBuilder.Entity<Compte>()
                .HasKey(c => c.IdCompte);

            modelBuilder.Entity<Compte>()
                .Property(c => c.IdCompte)
                .ValueGeneratedOnAdd();

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

            // Configuration des relations pour Transaction
            modelBuilder.Entity<Transaction>()
                .HasOne(t => t.Compte)
                .WithMany(c => c.Transactions)
                .HasForeignKey(t => t.IdCompte)
                .OnDelete(DeleteBehavior.Cascade);

            // Configuration des relations pour HistoriqueSolde
            modelBuilder.Entity<HistoriqueSolde>()
                .HasOne(h => h.Compte)
                .WithMany(c => c.HistoriquesSolde)
                .HasForeignKey(h => h.IdCompte)
                .OnDelete(DeleteBehavior.Cascade);

            // Configuration des relations pour HistoriqueStatutCompte
            modelBuilder.Entity<HistoriqueStatutCompte>()
                .HasOne(h => h.Compte)
                .WithMany(c => c.HistoriquesStatut)
                .HasForeignKey(h => h.IdCompte)
                .OnDelete(DeleteBehavior.Cascade);

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

            // Configuration pour Frais
            modelBuilder.Entity<Frais>()
                .Property(f => f.MontantMin)
                .HasPrecision(12, 2);

            modelBuilder.Entity<Frais>()
                .Property(f => f.MontantMax)
                .HasPrecision(12, 2);
        }
    }
}
