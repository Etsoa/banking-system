using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ServeurCompteDepot.Models
{
    [Table("transferts")]
    public class Transfert
    {
        [Key]
        [Column("id_transfert")]
        public int IdTransfert { get; set; }

        [Required]
        [Column("date_transfert")]
        public DateTime DateTransfert { get; set; } = DateTime.UtcNow.Date;

    [Required]
    [Column("montant", TypeName = "decimal(12,2)")]
    public decimal Montant { get; set; }

        [Required]
        [Column("envoyer")]
        public int Envoyer { get; set; }

        [Required]
        [Column("receveur")]
        public int Receveur { get; set; }

        // Navigation properties
        [ForeignKey("Envoyer")]
        public virtual Compte CompteEnvoyeur { get; set; } = null!;

        [ForeignKey("Receveur")]
        public virtual Compte CompteReceveur { get; set; } = null!;

        public virtual ICollection<Transaction> Transactions { get; set; } = new List<Transaction>();
    }
}