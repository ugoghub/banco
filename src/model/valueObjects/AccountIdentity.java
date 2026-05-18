package model.valueObjects;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public record AccountIdentity(String branch, String accountNumber) {

    public AccountIdentity {
        Objects.requireNonNull(branch);
        Objects.requireNonNull(accountNumber);
    }

    private static String generateBranch() {
        int branch = ThreadLocalRandom.current().nextInt(0, 10);

        return String.format("%04d", branch);
    }

    private static String generateAccountNumber() {
        String accountNumber = String.format("%06d",
                ThreadLocalRandom.current().nextInt(0, 1_000_000));

        return accountNumber + "-" + generateDigit(accountNumber);
    }

    private static int generateDigit(String accountNumber) {
        int sum = 0;

        for(char c : accountNumber.toCharArray()) {
            sum += Character.getNumericValue(c);
        }

        return sum % 10;
    }

    public static AccountIdentity generate() {
        return new AccountIdentity(generateBranch(), generateAccountNumber());
    }

    @Override
    public String toString() {
        return "Ag: " + branch + " | Conta: " + accountNumber;
    }
}