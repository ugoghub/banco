package model.valueobject;

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

        if (!accountNumber.matches("\\d{6}-\\d")  || !isValidDigit(accountNumber.replace("-", ""))) {
            throw new InvalidAccountNumberException("Número da conta inválido");
        }
    }

    private static boolean isValidDigit(String accountNumber){
        int digit = accountNumber.charAt(accountNumber.length() - 1) - '0';

        int sum = 0;

        for(int i = 0; i < accountNumber.length()-1; i++){
            sum += Character.getNumericValue(accountNumber.charAt(i));
        }

        int expectedDigit = sum % 10;

        return expectedDigit == digit;
    }

    @Override
    public String branch() {
        return branch;
    }

    @Override
    public String accountNumber() {
        return accountNumber;
    }
}