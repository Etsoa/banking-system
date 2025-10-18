namespace ServeurCompteDepot.Models
{
    public class TransactionsByCompteAndTypeRequest
    {
        public string CompteId { get; set; } = string.Empty;
        public int TypeId { get; set; }
    }
}