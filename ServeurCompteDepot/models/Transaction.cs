using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

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
        public DateTime DateTransaction { get; set; }

        [Required]
        [Column("montant", TypeName = "decimal(12,2)")]
        public decimal Montant { get; set; }

        [Required]
        [Column("id_type_transaction")]
        public int IdTypeTransaction { get; set; }

        [Required]
        [Column("id_compte")]
        public string IdCompte { get; set; } = string.Empty;

        // Navigation properties
        [ForeignKey("IdTypeTransaction")]
        public virtual TypeTransaction TypeTransaction { get; set; } = null!;

        [ForeignKey("IdCompte")]
        [JsonIgnore] // Evite la référence circulaire avec Compte
        public virtual Compte Compte { get; set; } = null!;

        [JsonIgnore] // On évite de sérialiser l'historique dans les transactions
        public virtual ICollection<HistoriqueSolde> HistoriquesSolde { get; set; } = new List<HistoriqueSolde>();
    }
}