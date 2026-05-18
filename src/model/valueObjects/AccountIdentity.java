package model.valueObjects;

import exception.ValidationException;
import util.AccountIdentityGenerator;

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

    private static String generateBranch() {
        return AccountIdentityGenerator.generateBranch();
    }

    private static String generateAccountNumber() {
        return AccountIdentityGenerator.generateAccountNumber();
    }

    public static AccountIdentity generate() {
        return new AccountIdentity(generateBranch(), generateAccountNumber());
    }

    @Override
    public String toString() {
        return "Ag: " + branch + " | Conta: " + accountNumber;
    }
}