using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ServeurCompteDepot.Models
{
    [Table("historiques_statut_compte")]
    public class HistoriqueStatutCompte
    {
        [Key]
        [Column("id_historique_statut_compte")]
        public int IdHistoriqueStatutCompte { get; set; }

        [Required]
        [Column("date_changement")]
        public DateTime DateChangement { get; set; } = DateTime.UtcNow;

        [Required]
        [Column("id_compte")]
        public string IdCompte { get; set; } = string.Empty;

        [Required]
        [Column("id_type_statut_compte")]
        public int IdTypeStatutCompte { get; set; }

        // Navigation properties
        [ForeignKey("IdCompte")]
        public virtual Compte Compte { get; set; } = null!;

        [ForeignKey("IdTypeStatutCompte")]
        public virtual TypeStatutCompte TypeStatutCompte { get; set; } = null!;
    }
}