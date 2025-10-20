using System.Text.Json.Serialization;

namespace ServeurCompteDepot.Models
{
    /// <summary>
    /// Modèle pour représenter une transaction avec les frais appliqués
    /// </summary>
    public class TransactionAvecFrais
    {
        [JsonPropertyName("idTransaction")]
        public int IdTransaction { get; set; }

        [JsonPropertyName("dateTransaction")]
        public DateTime DateTransaction { get; set; }

        [JsonPropertyName("montant")]
        public decimal Montant { get; set; }

        [JsonPropertyName("idTypeTransaction")]
        public int IdTypeTransaction { get; set; }

        [JsonPropertyName("idCompte")]
        public string IdCompte { get; set; } = string.Empty;

        [JsonPropertyName("typeTransactionLibelle")]
        public string? TypeTransactionLibelle { get; set; }

        [JsonPropertyName("fraisAppliques")]
        public decimal FraisAppliques { get; set; }

        [JsonPropertyName("nomFrais")]
        public string? NomFrais { get; set; }

        [JsonPropertyName("montantTotal")]
        public decimal MontantTotal { get; set; }

        [JsonPropertyName("hasFrais")]
        public bool HasFrais => FraisAppliques > 0;

        public TransactionAvecFrais()
        {
            FraisAppliques = 0;
            MontantTotal = 0;
        }

        public TransactionAvecFrais(Transaction transaction, TypeTransaction? typeTransaction = null)
        {
            IdTransaction = transaction.IdTransaction;
            DateTransaction = transaction.DateTransaction;
            Montant = transaction.Montant;
            IdTypeTransaction = transaction.IdTypeTransaction;
            IdCompte = transaction.IdCompte;
            TypeTransactionLibelle = typeTransaction?.Libelle ?? $"Type ID {transaction.IdTypeTransaction} non chargé";
            FraisAppliques = 0;
            NomFrais = null;
            MontantTotal = transaction.Montant;
        }

        public TransactionAvecFrais(Transaction transaction, TypeTransaction? typeTransaction, Frais? frais)
            : this(transaction, typeTransaction)
        {
            if (frais != null)
            {
                FraisAppliques = (decimal)frais.Valeur;
                NomFrais = frais.Nom;
                
                // Pour les débits, le montant total inclut les frais
                if (typeTransaction?.Signe == "-")
                {
                    MontantTotal = transaction.Montant + FraisAppliques;
                }
            }
        }
    }
}