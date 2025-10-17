using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ServeurCompteDepot.Models
{
    [Table("comptes")]
    public class Compte
    {
        [Column("id_num")]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int IdNum { get; set; }

        [Key]
        [Column("id_compte")]
        [DatabaseGenerated(DatabaseGeneratedOption.Computed)]
        public string IdCompte { get; set; } = string.Empty;

        [Required]
        [Column("date_ouverture")]
        public DateTime DateOuverture { get; set; } = DateTime.UtcNow;

        [Required]
        [Column("id_client")]
        public int IdClient { get; set; }

        [Required]
        [Column("solde", TypeName = "decimal(12,2)")]
        public decimal Solde { get; set; } = 0;

        // Navigation properties
        public virtual ICollection<Transaction> Transactions { get; set; } = new List<Transaction>();
        public virtual ICollection<Transfert> TransfertsEnvoyes { get; set; } = new List<Transfert>();
        public virtual ICollection<Transfert> TransfertsRecus { get; set; } = new List<Transfert>();
        public virtual ICollection<HistoriqueSolde> HistoriquesSolde { get; set; } = new List<HistoriqueSolde>();
        public virtual ICollection<HistoriqueStatutCompte> HistoriquesStatut { get; set; } = new List<HistoriqueStatutCompte>();
    }
}