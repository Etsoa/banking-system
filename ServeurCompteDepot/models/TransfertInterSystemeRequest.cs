namespace ServeurCompteDepot.Models
{
    public class TransfertInterSystemeRequest
    {
        public string DateTransfert { get; set; } = string.Empty;
        public string IdTransactionEnvoyeur { get; set; } = string.Empty;
        public string IdTransactionReceveur { get; set; } = string.Empty;
        public decimal Montant { get; set; }
        public string Envoyer { get; set; } = string.Empty;
        public string Receveur { get; set; } = string.Empty;
    }
}