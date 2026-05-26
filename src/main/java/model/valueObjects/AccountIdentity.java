package model.valueObjects;

import exception.InvalidAccountNumberException;
import exception.InvalidBranchException;
import exception.InvalidNullArgumentException;

public record AccountIdentity(String branch, String accountNumber) {

    public AccountIdentity {
        if(branch == null) throw new InvalidNullArgumentException("Agência não pode ser null");
        if(accountNumber == null) throw new InvalidNullArgumentException("Número da conta não pode ser null");

        branch = branch.trim();
        accountNumber = accountNumber.trim();

        if (branch.matches("\\d{2}")) {
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