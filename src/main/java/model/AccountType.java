package model;

public enum AccountType {
    CHECKING("Conta Corrente"),
    SAVINGS("Conta Poupança");

    private final String description;

    AccountType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}