package model.valueObjects;

import exception.ValidationException;
import generator.AccountIdentityGenerator;

import java.util.Objects;

public record AccountIdentity(String branch, String accountNumber) {

    public AccountIdentity {
        Objects.requireNonNull(branch);
        Objects.requireNonNull(accountNumber);

        if (!branch.matches("\\d{2}")){
            throw new ValidationException("Branch inválida");
        }

        if (!accountNumber.matches("\\d{6}-\\d")){
            throw new ValidationException("AcoountNumber inválida");
        }
    }

    public static AccountIdentity generate() {
        return AccountIdentityGenerator.generate();
    }

    @Override
    public String toString() {
        return "Ag: " + branch + " | Conta: " + accountNumber;
    }
}