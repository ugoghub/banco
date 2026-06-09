package ui.controller;

import ui.util.InputReader;
import ui.formatter.AccountIdentityFormatter;
import ui.menu.AccountMenu;
import ui.messages.ConsoleMessages;
import ui.selector.AccountSelector;
import exception.DomainException;
import model.AccountType;
import model.valueobject.AccountIdentity;
import model.valueobject.Cpf;
import application.ApplicationService;

import java.util.List;
import java.util.Scanner;

public final class AccountController {

    private final ApplicationService applicationService;
    private final Scanner scanner;
    private final AccountTransactionController transactionController;


    public AccountController(Scanner scanner,
                             ApplicationService applicationService,
                             AccountTransactionController transactionController) {
        this.scanner = scanner;
        this.applicationService = applicationService;
        this.transactionController = transactionController;
    }

    public void enterAccount(Cpf cpf) {
        try {
            List<AccountIdentity> accounts =
                    applicationService
                            .getClientAccountsIdentity(
                                    cpf
                            );

            AccountIdentity accountIdentity = AccountSelector.select(scanner, accounts);

            enterAccountMenu(accountIdentity);

        } catch (DomainException e) {
            ConsoleMessages.error(e);
        }
    }

    public void enterAccountMenu(AccountIdentity accountIdentity) {
        while (true) {
            AccountMenu.show(accountIdentity);

            int option = InputReader.readOption(scanner, o -> o >= 0 && o <= 5);

            switch (option) {

                case 1 -> transactionController.deposit(
                        accountIdentity
                );

                case 2 -> transactionController.withdraw(
                        accountIdentity
                );

                case 3 -> transactionController.showBalance(
                        accountIdentity
                );

                case 4 -> transactionController.transfer(
                        accountIdentity
                );

                case 5 -> transactionController.statement(
                        accountIdentity
                );

                case 0 -> {
                    return;
                }
            }
        }
    }

    public void createBankAccount(Cpf loggedCpf) {

        try {

            ConsoleMessages.infoLn("""
                    
                    ===== CRIAR CONTA =====
                    1 - Conta Corrente
                    2 - Conta Poupança
                    """
            );

            int option = InputReader.readOption(
                    scanner,
                    o -> o > 0 && o <= 2
            );

            AccountType type =
                    option == 1
                            ? AccountType.CHECKING
                            : AccountType.SAVINGS;

            AccountIdentity account = applicationService.createAccount(
                    loggedCpf,
                    type
            );

            ConsoleMessages.infoLn(
                    AccountIdentityFormatter
                            .format(
                                    account
                            )
            );

            ConsoleMessages.success(
                    "Conta criada com sucesso!"
            );


        } catch (DomainException e) {
            ConsoleMessages.error(e);
        }
    }

    public void removeBankAccount(Cpf loggedCpf) {

        try {

            List<AccountIdentity> accounts =
                    applicationService.getClientAccountsIdentity(
                            loggedCpf
                    );

            AccountIdentity accountIdentity = AccountSelector.select(scanner, accounts);

            applicationService.removeAccount(
                    accountIdentity
            );

            ConsoleMessages.success(
                    "Conta removida com sucesso!"
            );

        } catch (DomainException e) {
            ConsoleMessages.error(e);
        }
    }
}
