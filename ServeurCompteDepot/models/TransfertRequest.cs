using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace ServeurCompteDepot.Models
{
    public class TransfertRequest
    {
        [JsonPropertyName("compteEnvoyeur")]
        public string CompteEnvoyeur { get; set; } = string.Empty;
        
        [JsonPropertyName("compteReceveur")]
        public string CompteReceveur { get; set; } = string.Empty;
        
        [JsonPropertyName("montant")]
        public decimal Montant { get; set; }
        
        [JsonPropertyName("dateTransfert")]
        public DateTime DateTransfert { get; set; }
    }
}