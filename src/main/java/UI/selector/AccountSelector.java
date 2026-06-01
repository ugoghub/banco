package UI.selector;

import UI.InputReader;
import UI.messages.ConsoleMessages;
import exception.AccountNotFoundException;
import model.valueObjects.AccountIdentity;

import java.util.List;
import java.util.Scanner;

public final class AccountSelector {

    private AccountSelector() {
    }

    public static AccountIdentity select(
            Scanner scanner,
            List<AccountIdentity> accounts
    ) {

        if(accounts.isEmpty()){
            throw new AccountNotFoundException("Cliente não possui contas");
        }

        ConsoleMessages.info("Qual conta você deseja acessar: ");

        for (int i = 0; i < accounts.size(); i++) {
            System.out.printf(
                    "%d - %s%n",
                    i + 1,
                    accounts.get(i)
            );
        }

        int option = InputReader.readOption(
                scanner,
                o -> o > 0 && o <= accounts.size()
        );

        return accounts.get(option - 1);
    }
}
