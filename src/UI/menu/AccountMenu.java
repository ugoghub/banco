package UI.menu;

import UI.InputReader;
import UI.selector.AccountSelector;
import exception.*;
import model.Transaction;
import model.valueObjects.*;
import service.ApplicationService;

import java.util.List;
import java.util.Scanner;

public class AccountMenu {

    private final Scanner scanner;
    private final ApplicationService applicationService;

    public AccountMenu(
            Scanner scanner,
            ApplicationService applicationService
    ) {
        this.scanner = scanner;
        this.applicationService = applicationService;
    }

    public void show(AccountIdentity account) {

        while (true) {

            printMenu(account);

            int option = InputReader.readOption(
                    scanner,
                    o -> o >= 0 && o <= 5
            );

            switch (option) {
                case 1 -> deposit(account);
                case 2 -> withdraw(account);
                case 3 -> showBalance(account);
                case 4 -> transfer(account);
                case 5 -> showTransactions(account);
                case 0 -> {
                    return;
                }
            }
        }
    }

    private void printMenu(AccountIdentity account) {

        System.out.println("\n===== " + account + " =====");
        System.out.println("1 - Depositar");
        System.out.println("2 - Sacar");
        System.out.println("3 - Ver saldo");
        System.out.println("4 - Transferir");
        System.out.println("5 - Extrato");
        System.out.println("0 - Voltar");
        System.out.print("Escolha: ");
    }

    private void deposit(AccountIdentity account) {

        Money value = InputReader.readMoney(scanner, "Valor: ");

        try {

            applicationService.deposit(account, value);

            System.out.println("Depósito realizado!");

        } catch (
                InvalidAmountException |
                AccountNotFoundException e
        ) {

            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void withdraw(AccountIdentity account) {

        Money value = InputReader.readMoney(scanner, "Valor: ");

        try {

            applicationService.withdraw(account, value);

            System.out.println("Saque realizado!");

        } catch (
                InvalidAmountException |
                InsufficientBalanceException |
                AccountNotFoundException e
        ) {

            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void showBalance(AccountIdentity account) {

        try {

            System.out.println(
                    "Saldo: " +
                            applicationService.getAccountBalance(account)
            );

        } catch (AccountNotFoundException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void transfer(AccountIdentity account) {

        Cpf cpf = InputReader.readValidated(
                scanner,
                "CPF do destinatário: ",
                Cpf::new
        );

        try {

            List<AccountIdentity> accounts =
                    applicationService.getClientAccountsIdentity(cpf);

            if (accounts.isEmpty()) {
                System.out.println("Cliente não possui contas");
                return;
            }

            AccountIdentity destination = AccountSelector.select(
                    scanner,
                    accounts,
                    "Escolha a conta destino: "
            );

            Money value = InputReader.readMoney(
                    scanner,
                    "Valor da transferência: "
            );

            applicationService.transfer(
                    account,
                    destination,
                    value
            );

            System.out.println("Transferência realizada!");

        } catch (
                InvalidAmountException |
                InvalidTransferException |
                InsufficientBalanceException |
                ClientNotFoundException |
                AccountNotFoundException e
        ) {

            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void showTransactions(AccountIdentity account) {

        try {

            List<Transaction> history =
                    applicationService.getAccountTransactions(account);

            if (history.isEmpty()) {
                System.out.println("Conta sem extrato");
                return;
            }

            for (Transaction transaction : history) {
                System.out.println(transaction);
            }

        } catch (AccountNotFoundException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}