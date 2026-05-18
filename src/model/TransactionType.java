package model;

public enum TransactionType {
    DEPOSIT("Depósito"),
    INTEREST("Rendimento"),
    TRANSFER("Transferência"),
    WITHDRAW("Saque");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}