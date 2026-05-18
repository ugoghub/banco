package util;

import java.util.concurrent.ThreadLocalRandom;

public abstract class AccountIdentityGenerator {
    public static String generateBranch() {
        int branch = ThreadLocalRandom.current().nextInt(0, 10);

        return String.format("%02d", branch);
    }

    public static String generateAccountNumber() {
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
}
