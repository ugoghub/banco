package UI.menu;

import UI.InputReader;
import UI.error.ErrorHandler;
import exception.DomainException;
import model.valueObjects.AccountIdentity;
import model.valueObjects.Cpf;
import model.valueObjects.Money;
import service.ApplicationService;
import service.dto.StatementData;

import java.util.List;
import java.util.Scanner;

public class AccountMenu {

    public static void start(
            Scanner scanner,
            ApplicationService applicationService,
            Cpf cpf
    ) {

        List<AccountIdentity> accounts =
                applicationService
                        .getClientAccountsIdentity(
                                cpf
                        );

        if (accounts.isEmpty()) {

            System.out.println(
                    "Cliente não possui contas"
            );

            return;
        }

        int i = 1;

        for (AccountIdentity accountIdentity : accounts) {

            System.out.printf(
                    "%d - %s\n",
                    i++,
                    accountIdentity
            );
        }

        System.out.print("\nEscolha: ");

        int choice = InputReader.readOption(
                scanner,
                c -> c > 0 && c <= accounts.size()
        );

        AccountIdentity accountIdentity =
                accounts.get(choice - 1);

        menuLoop(
                scanner,
                applicationService,
                accountIdentity
        );
    }

    private static void menuLoop(
            Scanner scanner,
            ApplicationService applicationService,
            AccountIdentity accountIdentity
    ) {

        while (true) {

            System.out.println("""

                    ===== CONTA =====
                    1 - Depositar
                    2 - Sacar
                    3 - Ver saldo
                    4 - Transferir
                    5 - Extrato
                    0 - Voltar
                    """);

            System.out.print("Escolha: ");

            int option = InputReader.readOption(
                    scanner,
                    o -> o >= 0 && o <= 5
            );

            switch (option) {

                case 1 -> deposit(
                        scanner,
                        applicationService,
                        accountIdentity
                );

                case 2 -> withdraw(
                        scanner,
                        applicationService,
                        accountIdentity
                );

                case 3 -> showBalance(
                        applicationService,
                        accountIdentity
                );

                case 4 -> transfer(
                        scanner,
                        applicationService,
                        accountIdentity
                );

                case 5 -> statement(
                        applicationService,
                        accountIdentity
                );

                case 0 -> {
                    return;
                }
            }
        }
    }

    private static void deposit(
            Scanner scanner,
            ApplicationService applicationService,
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

            System.out.println(
                    "Depósito realizado!"
            );

        } catch (DomainException e) {
            ErrorHandler.printError(e);
        }
    }

    private static void withdraw(
            Scanner scanner,
            ApplicationService applicationService,
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

            System.out.println(
                    "Saque realizado!"
            );

        } catch (
                DomainException e
        ) {
            ErrorHandler.printError(e);
        }
    }

    private static void showBalance(
            ApplicationService applicationService,
            AccountIdentity accountIdentity
    ) {

        try {

            System.out.println(
                    "Saldo: "
                            + applicationService
                            .getAccountBalance(accountIdentity)
            );

        } catch (DomainException e) {
            ErrorHandler.printError(e);
        }
    }

    private static void transfer(
            Scanner scanner,
            ApplicationService applicationService,
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

            System.out.println(
                    "Transferência realizada!"
            );

        } catch (DomainException e) {
            ErrorHandler.printError(e);
        }
    }

    private static void statement(
            ApplicationService applicationService,
            AccountIdentity accountIdentity
    ) {

        try {

            List<StatementData> transactions =
                    applicationService
                            .getAccountTransactions(
                                    accountIdentity
                            );

            if (transactions.isEmpty()) {

                System.out.println(
                        "Conta sem extrato"
                );

                return;
            }

            for (StatementData transaction
                    : transactions) {

                System.out.println(transaction);
            }

        } catch (DomainException e) {
            ErrorHandler.printError(e);
        }
    }
}