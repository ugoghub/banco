package UI.menu;

import UI.InputReader;
import exception.*;
import model.Transaction;
import model.valueObjects.AccountIdentity;
import model.valueObjects.Cpf;
import model.valueObjects.Money;
import service.ApplicationService;

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

        for (AccountIdentity account : accounts) {

            System.out.printf(
                    "%d - %s\n",
                    i++,
                    account
            );
        }

        System.out.print("\nEscolha: ");

        int choice = InputReader.readOption(
                scanner,
                c -> c > 0 && c <= accounts.size()
        );

        AccountIdentity account =
                accounts.get(choice - 1);

        menuLoop(
                scanner,
                applicationService,
                account
        );
    }

    private static void menuLoop(
            Scanner scanner,
            ApplicationService applicationService,
            AccountIdentity account
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
                        account
                );

                case 2 -> withdraw(
                        scanner,
                        applicationService,
                        account
                );

                case 3 -> showBalance(
                        applicationService,
                        account
                );

                case 4 -> transfer(
                        scanner,
                        applicationService,
                        account
                );

                case 5 -> statement(
                        applicationService,
                        account
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
            AccountIdentity account
    ) {

        try {

            Money value =
                    InputReader.readMoney(
                            scanner,
                            "Valor: "
                    );

            applicationService.deposit(
                    account,
                    value
            );

            System.out.println(
                    "Depósito realizado!"
            );

        } catch (
                InvalidAmountException |
                AccountNotFoundException e
        ) {

            System.out.println(
                    "Erro: " + e.getMessage()
            );
        }
    }

    private static void withdraw(
            Scanner scanner,
            ApplicationService applicationService,
            AccountIdentity account
    ) {

        try {

            Money value =
                    InputReader.readMoney(
                            scanner,
                            "Valor: "
                    );

            applicationService.withdraw(
                    account,
                    value
            );

            System.out.println(
                    "Saque realizado!"
            );

        } catch (
                InvalidAmountException |
                InsufficientBalanceException |
                AccountNotFoundException e
        ) {

            System.out.println(
                    "Erro: " + e.getMessage()
            );
        }
    }

    private static void showBalance(
            ApplicationService applicationService,
            AccountIdentity account
    ) {

        try {

            System.out.println(
                    "Saldo: "
                            + applicationService
                            .getAccountBalance(account)
            );

        } catch (AccountNotFoundException e) {

            System.out.println(
                    "Erro: " + e.getMessage()
            );
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

        } catch (
                InvalidAmountException |
                InvalidTransferException |
                InsufficientBalanceException |
                AccountNotFoundException |
                ValidationException e
        ) {

            System.out.println(
                    "Erro: " + e.getMessage()
            );
        }
    }

    private static void statement(
            ApplicationService applicationService,
            AccountIdentity account
    ) {

        try {

            List<Transaction> transactions =
                    applicationService
                            .getAccountTransactions(
                                    account
                            );

            if (transactions.isEmpty()) {

                System.out.println(
                        "Conta sem extrato"
                );

                return;
            }

            for (Transaction transaction
                    : transactions) {

                System.out.println(transaction);
            }

        } catch (AccountNotFoundException e) {

            System.out.println(
                    "Erro: " + e.getMessage()
            );
        }
    }
}