using System.ComponentModel.DataAnnotations;

namespace ServeurCompteDepot.Models
{
    public class TransactionRequest
    {
        [Required]
        public DateTime DateTransaction { get; set; }

        [Required]
        public decimal Montant { get; set; }

        [Required]
        public int IdTypeTransaction { get; set; }

        [Required]
        public string IdCompte { get; set; } = string.Empty;
    }
}