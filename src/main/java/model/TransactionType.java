package model;

public enum TransactionType {
    DEPOSIT("Depósito"),
    INTEREST("Rendimento"),
    TRANSFER_SENT("Transferência Enviada"),
    TRANSFER_RECEIVED("Transferência Recebida"),
    WITHDRAW("Saque");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}