package ui.menu;

import ui.InputReader;
import ui.formatter.AccountIdentityFormatter;
import ui.messages.ConsoleMessages;
import exception.NoAccountsFoundException;
import model.valueobject.AccountIdentity;

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
            throw new NoAccountsFoundException("Cliente não possui contas");
        }

        ConsoleMessages.infoLn("Qual conta você deseja acessar: ");

        for (int i = 0; i < accounts.size(); i++) {
            ConsoleMessages.highlight(
                    "%d - %s",
                    i + 1,
                    AccountIdentityFormatter.format(accounts.get(i))
            );
        }

        int option = InputReader.readOption(
                scanner,
                o -> o > 0 && o <= accounts.size()
        );

        return accounts.get(option - 1);
    }
}
