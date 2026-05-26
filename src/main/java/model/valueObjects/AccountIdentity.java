package model.valueObjects;

import exception.InvalidAccountNumberException;
import exception.InvalidBranchException;

public record AccountIdentity(String branch, String accountNumber) {

    public AccountIdentity {

        if (branch == null) {
            throw new InvalidBranchException("Agência inválida");
        }

        if (accountNumber == null) {
            throw new InvalidAccountNumberException("Número da conta inválido");
        }

        branch = branch.trim();
        accountNumber = accountNumber.trim();

        if (!branch.matches("\\d{2}")) {
            throw new InvalidBranchException("Agência inválida");
        }

        if (!accountNumber.matches("\\d{6}-\\d")) {
            throw new InvalidAccountNumberException("Número da conta inválido");
        }
    }

    @Override
    public String toString() {
        return "Ag: " + branch + " | Conta: " + accountNumber;
    }
}