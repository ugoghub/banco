package model.valueObjects;

import exception.InvalidAccountNumberException;
import exception.InvalidBranchException;

public record AccountIdentity(String branch, String accountNumber) {

    public AccountIdentity {
        if (branch == null || !branch.matches("\\d{2}")) {
            throw new InvalidBranchException("Agência inválida");
        }
        if (accountNumber == null || !accountNumber.matches("\\d{6}-\\d")) {
            throw new InvalidAccountNumberException("Número da conta inválido");
        }

        branch = branch.trim();
        accountNumber = accountNumber.trim();
    }

    @Override
    public String toString() {
        return "Ag: " + branch + " | Conta: " + accountNumber;
    }
}