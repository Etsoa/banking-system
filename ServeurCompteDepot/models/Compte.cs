using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ServeurCompteDepot.Models
{
    [Table("comptes")]
    public class Compte
    {
        [Key]
        [Column("id_compte")]
        public int IdCompte { get; set; }

        [Required]
        [Column("date_ouverture")]
        public DateTime DateOuverture { get; set; } = DateTime.UtcNow;

        [Required]
        [Column("id_client")]
        public int IdClient { get; set; }

        [Required]
        [Column("solde")]
        [Column(TypeName = "decimal(12,2)")]
        public decimal Solde { get; set; } = 0;

        // Navigation properties
        public virtual ICollection<Transaction> Transactions { get; set; } = new List<Transaction>();
        public virtual ICollection<Transfert> TransfertsEnvoyes { get; set; } = new List<Transfert>();
        public virtual ICollection<Transfert> TransfertsRecus { get; set; } = new List<Transfert>();
        public virtual ICollection<HistoriqueSolde> HistoriquesSolde { get; set; } = new List<HistoriqueSolde>();
        public virtual ICollection<HistoriqueStatutCompte> HistoriquesStatut { get; set; } = new List<HistoriqueStatutCompte>();
    }
}