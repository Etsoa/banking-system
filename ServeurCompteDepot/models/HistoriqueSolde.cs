using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace ServeurCompteDepot.Models
{
    [Table("historiques_solde")]
    public class HistoriqueSolde
    {
        [Key]
        [Column("id_historique_solde")]
        public int IdHistoriqueSolde { get; set; }

    [Required]
    [Column("montant", TypeName = "decimal(12,2)")]
    public decimal Montant { get; set; }

        [Required]
        [Column("date_changement")]
        public DateTime DateChangement { get; set; } = DateTime.UtcNow;

        [Required]
        [Column("id_compte")]
        public string IdCompte { get; set; } = string.Empty;

        [Required]
        [Column("id_transaction")]
        public int IdTransaction { get; set; }

        // Navigation properties
        [ForeignKey("IdCompte")]
        [JsonIgnore] // Evite la référence circulaire avec Compte
        public virtual Compte Compte { get; set; } = null!;

        [ForeignKey("IdTransaction")]
        [JsonIgnore] // Evite la référence circulaire avec Transaction
        public virtual Transaction Transaction { get; set; } = null!;
    }
}