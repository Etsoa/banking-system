using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ServeurCompteDepot.Models
{
    [Table("types_transaction")]
    public class TypeTransaction
    {
        [Key]
        [Column("id_type_transaction")]
        public int IdTypeTransaction { get; set; }

        [Required]
        [MaxLength(50)]
        [Column("libelle")]
        public string Libelle { get; set; } = string.Empty;

        [Required]
        [Column("actif")]
        public bool Actif { get; set; } = true;

        [Required]
        [MaxLength(1)]
        [Column("signe")]
        public string Signe { get; set; } = string.Empty;

        // Navigation properties
        public virtual ICollection<Transaction> Transactions { get; set; } = new List<Transaction>();
    }
}