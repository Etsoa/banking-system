using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ServeurCompteDepot.Models
{
    [Table("transactions")]
    public class Transaction
    {
        [Key]
        [Column("id_transaction")]
        public int IdTransaction { get; set; }

        [Required]
        [Column("date_transaction")]
        public DateTime DateTransaction { get; set; } = DateTime.UtcNow;

        [Required]
        [Column("montant")]
        [Column(TypeName = "decimal(12,2)")]
        public decimal Montant { get; set; }

        [Required]
        [Column("Id_type_transaction")]
        public int IdTypeTransaction { get; set; }

        [Required]
        [Column("id_compte")]
        public int IdCompte { get; set; }

        [Column("id_transfert")]
        public int? IdTransfert { get; set; }

        // Navigation properties
        [ForeignKey("IdTypeTransaction")]
        public virtual TypeTransaction TypeTransaction { get; set; } = null!;

        [ForeignKey("IdCompte")]
        public virtual Compte Compte { get; set; } = null!;

        [ForeignKey("IdTransfert")]
        public virtual Transfert? Transfert { get; set; }

        public virtual ICollection<HistoriqueSolde> HistoriquesSolde { get; set; } = new List<HistoriqueSolde>();
    }
}