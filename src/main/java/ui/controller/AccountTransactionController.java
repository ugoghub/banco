package ui.controller;

import application.ApplicationService;
import exception.DomainException;
import model.valueobject.AccountIdentity;
import model.valueobject.Money;
import service.dto.StatementData;
import ui.formatter.MoneyFormatter;
import ui.formatter.StatementFormatter;
import ui.util.InputReader;
import ui.messages.ConsoleMessages;

import java.util.List;
import java.util.Scanner;

public final class AccountTransactionController {

    private final Scanner scanner;
    private final ApplicationService applicationService;

    public AccountTransactionController(Scanner scanner, ApplicationService applicationService) {
        this.scanner = scanner;
        this.applicationService = applicationService;
    }

    public void deposit(
            AccountIdentity accountIdentity
    ) {

        try {

            Money value =
                    InputReader.readMoney(
                            scanner,
                            "Valor: "
                    );

            applicationService.deposit(
                    accountIdentity,
                    value
            );

            ConsoleMessages.success("Depósito Realizado!");

        } catch (DomainException e) {
            ConsoleMessages.error(e);
        }
    }

    public void withdraw(
            AccountIdentity accountIdentity
    ) {

        try {

            Money value =
                    InputReader.readMoney(
                            scanner,
                            "Valor: "
                    );

            applicationService.withdraw(
                    accountIdentity,
                    value
            );

            ConsoleMessages.success("Saque Realizado!");

        } catch (DomainException e) {
            ConsoleMessages.error(e);
        }
    }

    public void showBalance(
            AccountIdentity accountIdentity
    ) {

        try {

            Money accountBalance = applicationService.getAccountBalance(accountIdentity);

            ConsoleMessages.highlight(
                    "Saldo: "
                            + MoneyFormatter.format(accountBalance)
                    );

        } catch (DomainException e) {
            ConsoleMessages.error(e);
        }
    }

    public void transfer(
            AccountIdentity from
    ) {

        try {

            String branch =
                    InputReader.readValidated(
                            scanner,
                            "Agência destino: ",
                            String::valueOf
                    );

            String number =
                    InputReader.readValidated(
                            scanner,
                            "Conta destino: ",
                            String::valueOf
                    );

            AccountIdentity to =
                    new AccountIdentity(
                            branch,
                            number
                    );

            Money value =
                    InputReader.readMoney(
                            scanner,
                            "Valor: "
                    );

            applicationService.transfer(
                    from,
                    to,
                    value
            );

            ConsoleMessages.success("Transferência Realizada!");

        } catch (DomainException e) {
            ConsoleMessages.error(e);
        }
    }

    public void statement(
            AccountIdentity accountIdentity
    ) {

        try {

            List<StatementData> transactions =
                    applicationService
                            .getAccountTransactions(
                                    accountIdentity
                            );

            if (transactions.isEmpty()) {

                ConsoleMessages.highlight("Conta sem extrato!");

                return;
            }

            for (StatementData transaction
                    : transactions) {

                ConsoleMessages.highlight(
                        StatementFormatter.format(transaction)
                );

            }

        } catch (DomainException e) {
            ConsoleMessages.error(e);
        }
    }
}
