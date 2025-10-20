package com.example.centralizer.dto;

/**
 * Enum TypeTransaction
 */
public enum TypeTransaction {
    RETRAIT("retrait"),
    DEPOT("depot");
    
    private final String value;
    
    TypeTransaction(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static TypeTransaction fromValue(String value) {
        for (TypeTransaction type : TypeTransaction.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Type de transaction inconnu: " + value);
    }
}
