using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ServeurCompteDepot.Models
{
    [Table("frais")]
    public class Frais
    {
        [Key]
        [Column("id_frais")]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int Id { get; set; }

        [Required]
        [Column("date_debut")]
        public DateTime DateDebut { get; set; }

        [Required]
        [Column("nom")]
        [MaxLength(50)]
        public string Nom { get; set; } = string.Empty;

        [Required]
        [Column("montant_min", TypeName = "decimal(12,2)")]
        public decimal MontantMin { get; set; }

        [Required]
        [Column("montant_max", TypeName = "decimal(12,2)")]
        public decimal MontantMax { get; set; }

        [Required]
        [Column("valeur")]
        public int Valeur { get; set; }
    }
}