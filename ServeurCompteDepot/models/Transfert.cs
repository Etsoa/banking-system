using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

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
        public DateTime DateTransfert { get; set; }

        [Required]
        [Column("id_transaction_envoyeur")]
        public string IdTransactionEnvoyeur { get; set; } = string.Empty;

        [Required]
        [Column("id_transaction_receveur")]
        public string IdTransactionReceveur { get; set; } = string.Empty;

        [Required]
        [Column("montant", TypeName = "decimal(12,2)")]
        public decimal Montant { get; set; }

        [Required]
        [Column("envoyer")]
        public string Envoyer { get; set; } = string.Empty;

        [Required]
        [Column("receveur")]
        public string Receveur { get; set; } = string.Empty;

        // Navigation properties
        [ForeignKey("Envoyer")]
        [JsonIgnore] // Evite la référence circulaire avec Compte
        public virtual Compte CompteEnvoyeur { get; set; } = null!;

        [ForeignKey("Receveur")]
        [JsonIgnore] // Evite la référence circulaire avec Compte
        public virtual Compte CompteReceveur { get; set; } = null!;
    }
}