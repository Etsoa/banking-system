using System.ComponentModel.DataAnnotations;
using ServeurCompteDepot.Models;

namespace ServeurCompteDepot.models
{
    /// <summary>
    /// Modèle représentant un transfert avec les frais associés
    /// </summary>
    public class TransfertAvecFrais
    {
        public int IdTransfert { get; set; }
        
        [Required]
        public string CompteEnvoyeur { get; set; } = string.Empty;
        
        [Required]
        public string CompteReceveur { get; set; } = string.Empty;
        
        [Required]
        public decimal Montant { get; set; }
        
        [Required]
        public DateTime DateTransfert { get; set; }
        
        // Informations sur les frais
        public decimal FraisEnvoyeur { get; set; }
        public string LibelleFraisEnvoyeur { get; set; } = string.Empty;
        public decimal MontantTotalEnvoyeur { get; set; } // montant + frais
        
        public TransfertAvecFrais()
        {
        }
        
        // Constructeur avec transfert de base
        public TransfertAvecFrais(Transfert transfert)
        {
            IdTransfert = transfert.IdTransfert;
            CompteEnvoyeur = transfert.Envoyer;
            CompteReceveur = transfert.Receveur;
            Montant = transfert.Montant;
            DateTransfert = transfert.DateTransfert;
        }
    }
}