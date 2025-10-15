namespace ServeurCompteDepot.Models
{
    public class CompteAvecStatut
    {
        public int IdCompte { get; set; }
        public DateTime DateOuverture { get; set; }
        public int IdClient { get; set; }
        public decimal Solde { get; set; }
        public string StatutActuel { get; set; } = "Inconnu";
        public DateTime? DateChangementStatut { get; set; }
        public bool EstActif { get; set; } = true;
    }
}