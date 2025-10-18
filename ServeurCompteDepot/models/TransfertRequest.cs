using System.ComponentModel.DataAnnotations;

namespace ServeurCompteDepot.Models
{
    public class TransfertRequest
    {
        public string CompteEnvoyeur { get; set; } = string.Empty;
        public string CompteReceveur { get; set; } = string.Empty;
        public decimal Montant { get; set; }
        
        [Required]
        public DateTime DateTransfert { get; set; }
    }
}