package UI.selector;

import UI.InputReader;
import model.valueObjects.AccountIdentity;

import java.util.List;
import java.util.Scanner;

public class AccountSelector {

    public static AccountIdentity select(
            Scanner scanner,
            List<AccountIdentity> accounts,
            String message
    ) {

        if (accounts.isEmpty()) {
            return null;
        }

        int i = 1;

        for (AccountIdentity account : accounts) {
            System.out.printf("%d - %s%n", i++, account);
        }

        System.out.println(message);

        int choice = InputReader.readOption(
                scanner,
                c -> c > 0 && c <= accounts.size()
        );

        return accounts.get(choice - 1);
    }
}