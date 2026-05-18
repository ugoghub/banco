package model.valueObject;

import java.util.Objects;

public record AccountIdentity(String branch, String accountNumber) {

    public AccountIdentity{
        Objects.requireNonNull(branch);
        Objects.requireNonNull(accountNumber);
    }

    @Override
    public String toString() {
        return "Ag: " + branch + " | Conta: " + accountNumber;
    }
}