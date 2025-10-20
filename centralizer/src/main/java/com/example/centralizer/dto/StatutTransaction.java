package com.example.centralizer.dto;

/**
 * Enum StatutTransaction
 */
public enum StatutTransaction {
    EN_ATTENTE("en_attente"),
    CONFIRMEE("confirmee"),
    REFUSEE("refusee");
    
    private final String value;
    
    StatutTransaction(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static StatutTransaction fromValue(String value) {
        for (StatutTransaction statut : StatutTransaction.values()) {
            if (statut.value.equalsIgnoreCase(value)) {
                return statut;
            }
        }
        throw new IllegalArgumentException("Statut de transaction inconnu: " + value);
    }
}
