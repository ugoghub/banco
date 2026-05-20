package model.valueObjects;

import exception.InvalidAccountNumberException;
import exception.InvalidBranchException;

import java.util.Objects;

public record AccountIdentity(String branch, String accountNumber) {

    public AccountIdentity {
        Objects.requireNonNull(branch, "Branch não pode ser null");
        Objects.requireNonNull(accountNumber, "AccountNumber não pode ser null");

        if (!branch.matches("\\d{2}")){
            throw new InvalidBranchException("Branch inválida");
        }

        if (!accountNumber.matches("\\d{6}-\\d")){
            throw new InvalidAccountNumberException("AccountNumber inválida");
        }
    }

    @Override
    public String toString() {
        return "Ag: " + branch + " | Conta: " + accountNumber;
    }
}