package model.valueObjects;

import java.util.concurrent.ThreadLocalRandom;

public final class AccountIdentityFactory {

    private static final int MAX_ACCOUNT_NUMBER = 1_000_000;
    private static final int MAX_BRANCH_NUMBER = 50;

    private AccountIdentityFactory() {
    }

    private static String generateBranch() {
        int branch = ThreadLocalRandom.current().nextInt(0, MAX_BRANCH_NUMBER);

        return String.format("%02d", branch);
    }

    private static String generateAccountNumber() {
        String accountNumber = String.format("%06d",
                ThreadLocalRandom.current().nextInt(0, MAX_ACCOUNT_NUMBER));

        return accountNumber + "-" + generateDigit(accountNumber);
    }

    private static int generateDigit(String accountNumber) {
        int sum = 0;

        for(char c : accountNumber.toCharArray()) {
            sum += Character.getNumericValue(c);
        }

        return sum % 10;
    }

    public static AccountIdentity generate(){
        return new AccountIdentity(generateBranch(), generateAccountNumber());
    }
}
