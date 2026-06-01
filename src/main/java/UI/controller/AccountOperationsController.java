package UI.controller;

import UI.InputReader;
import UI.messages.ConsoleMessages;
import exception.DomainException;
import model.valueObjects.AccountIdentity;
import model.valueObjects.Money;
import service.ApplicationService;
import service.dto.StatementData;

import java.util.List;
import java.util.Scanner;

public final class AccountOperationsController {

    private final Scanner scanner;
    private final ApplicationService applicationService;

    public AccountOperationsController(Scanner scanner, ApplicationService applicationService) {
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

        } catch (
                DomainException e
        ) {
            ConsoleMessages.error(e);
        }
    }

    public void showBalance(
            AccountIdentity accountIdentity
    ) {

        try {

            System.out.println(
                    "Saldo: "
                            + applicationService
                            .getAccountBalance(accountIdentity)
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
                            s -> s
                    );

            String number =
                    InputReader.readValidated(
                            scanner,
                            "Conta destino: ",
                            s -> s
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

                ConsoleMessages.info("Conta sem extrato!");

                return;
            }

            for (StatementData transaction
                    : transactions) {

                System.out.println(transaction);
            }

        } catch (DomainException e) {
            ConsoleMessages.error(e);
        }
    }
}
