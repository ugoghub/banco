package model.valueObject;

import java.util.Objects;

public record AccountIdentity(String branch, String accountNumber) {

    public AccountIdentity(String branch, String accountNumber){
        Objects.requireNonNull(branch);
        Objects.requireNonNull(accountNumber);

        this.branch = branch;
        this.accountNumber = accountNumber;
    }

    @Override
    public String toString() {
        return "Ag: " + branch + " | Conta: " + accountNumber;
    }
}
