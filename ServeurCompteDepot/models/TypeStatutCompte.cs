using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ServeurCompteDepot.Models
{
    [Table("types_statut_compte")]
    public class TypeStatutCompte
    {
        [Key]
        [Column("id_type_statut_compte")]
        public int IdTypeStatutCompte { get; set; }

        [Required]
        [MaxLength(50)]
        [Column("libelle")]
        public string Libelle { get; set; } = string.Empty;

        [Required]
        [Column("actif")]
        public bool Actif { get; set; } = true;

        // Navigation properties
        public virtual ICollection<HistoriqueStatutCompte> HistoriquesStatut { get; set; } = new List<HistoriqueStatutCompte>();
    }
}