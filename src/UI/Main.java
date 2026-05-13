package UI;

import exception.*;
import model.Account;
import model.AccountType;
import model.Transaction;
import model.valueObject.AccountIdentity;
import model.valueObject.Cpf;
import model.valueObject.Money;
import service.ApplicationService;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ApplicationService applicationService = new ApplicationService();

        while (true) {
            System.out.println("\n===== BANKLITE =====");
            System.out.println("1 - Criar cliente");
            System.out.println("2 - Criar conta");
            System.out.println("3 - Acessar conta");
            System.out.println("4 - Excluir Cliente");
            System.out.println("5 - Excluir Conta");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            int option = InputReader.readOption(scanner, o -> o >= 0 && o <= 5);

            switch (option) {

                // CRIAR CLIENTE
                case 1:

                    String name = InputReader.readString(scanner, "Nome completo: ");

                    Cpf cpf = InputReader.readCpf(scanner, "CPF: ");

                    String email = InputReader.readEmail(scanner, "Email: ");

                    try {
                        applicationService.createClient(name, cpf, email);
                        System.out.println("Cliente cadastrado com sucesso!");
                    } catch (CpfAlreadyExistsException | InvalidCpfException e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                    break;

                // CRIAR CONTA
                case 2:

                    Cpf cpfAccount = InputReader.readCpf(scanner, "CPF do cliente: ");

                    try {
                        System.out.println("Tipo da conta:");
                        System.out.println("1 - Conta Corrente");
                        System.out.println("2 - Conta Poupança");
                        System.out.print("Escolha: ");

                        int typeOption = InputReader.readOption(scanner, t -> t > 0 && t <= 2);

                        AccountType type = (typeOption == 1)
                                ? AccountType.CHECKING
                                : AccountType.SAVINGS;

                        Account account = applicationService.createAccount(cpfAccount, type);

                        System.out.println("\nConta criada com sucesso!\n" + account);

                    } catch (ClientNotFoundException e) {
                        System.out.println("Erro: " + e.getMessage());
                    }

                    break;

                // ACESSAR CONTA
                case 3:

                    Cpf cpfClient = InputReader.readCpf(scanner, "Digite seu CPF: ");

                    try {
                        List<AccountIdentity> accounts = applicationService.getClientAccountsIdentity(cpfClient);

                        if (accounts.isEmpty()) {
                            System.out.println("Cliente não possui contas");
                            break;
                        }

                        int i = 1;

                        for (AccountIdentity account : accounts) {
                            System.out.printf("%d - %s\n", i++, account);
                        }

                        System.out.println("Escolha o id da conta que você deseja acessar: ");

                        int choice = InputReader.readOption(scanner,
                                c -> c > 0 && c <= accounts.size());

                        AccountIdentity accountIdentity = accounts.get(choice - 1);

                        accountMenu(scanner, applicationService, accountIdentity);

                    } catch (InvalidCpfException | ClientNotFoundException e) {
                        System.out.println("Erro: " + e.getMessage());
                    }

                    break;

                case 4:
                    Cpf clientCpf = InputReader.readCpf(scanner, "Digite seu Cpf para excluir sua conta: ");
                    try {
                        applicationService.removeClient(clientCpf);
                    } catch (ClientNotFoundException | AccountDeletionNotAllowedException e) {
                        System.out.println("Erro "+ e.getMessage());
                    }
                    break;

                case 5:
                    Cpf cpfCli = InputReader.readCpf(scanner, "Digite seu Cpf: ");

                    try {
                        List<AccountIdentity> accounts = applicationService.getClientAccountsIdentity(cpfCli);

                        if (accounts.isEmpty()) {
                            System.out.println("Cliente não possui contas");
                            break;
                        }

                        int i = 1;

                        for (AccountIdentity account : accounts) {
                            System.out.printf("%d - %s\n", i++, account);
                        }

                        System.out.println("Escolha o id da conta que você deseja excluir: ");

                        int choice = InputReader.readOption(scanner,
                                c -> c > 0 && c <= accounts.size());

                        applicationService.removeClientAccount(accounts.get(choice-1));


                    } catch (InvalidCpfException | AccountNotFoundException | ClientNotFoundException |
                             AccountDeletionNotAllowedException e) {
                        System.out.println("Erro: " + e.getMessage());
                    }

                    break;


                case 0:
                    System.out.println("Saindo...");
                    return;

                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    // MENU DA CONTA
    private static void accountMenu(Scanner scanner,
                                    ApplicationService applicationService,
                                    AccountIdentity account) {

        while (true) {

            System.out.println("\n===== CONTA " + account + " =====");
            System.out.println("1 - Depositar");
            System.out.println("2 - Sacar");
            System.out.println("3 - Ver saldo");
            System.out.println("4 - Transferir");
            System.out.println("5 - Extrato");
            System.out.println("0 - Voltar");

            System.out.print("Escolha: ");

            int option = InputReader.readOption(scanner, o -> o >= 0 && o <= 5);

            switch (option) {

                case 1:

                    Money depositValue =
                            InputReader.readMoney(scanner, "Valor: ");

                    try {
                        applicationService.deposit(account, depositValue);
                        System.out.println("Depósito realizado!");
                    } catch (InvalidAmountException |
                             AccountNotFoundException e) {

                        System.out.println("Erro: " + e.getMessage());
                    }

                    break;

                case 2:

                    Money withdrawValue =
                            InputReader.readMoney(scanner, "Valor: ");

                    try {
                        applicationService.withdraw(account, withdrawValue);
                        System.out.println("Saque realizado!");

                    } catch (InvalidAmountException |
                             InsufficientBalanceException |
                             AccountNotFoundException e) {

                        System.out.println("Erro: " + e.getMessage());
                    }

                    break;

                case 3:

                    try {
                        System.out.println(
                                "Saldo: R$ " +
                                        applicationService
                                                .getAccountBalance(account));

                    } catch (AccountNotFoundException e) {
                        System.out.println("Erro: " + e.getMessage());
                    }

                    break;

                case 4:

                    Cpf cpfTransfer =
                            InputReader.readCpf(scanner,
                                    "Para quem você deseja transferir? (CPF): ");

                    List<AccountIdentity> accounts =
                            applicationService.getClientAccountsIdentity(cpfTransfer);

                    if (accounts.isEmpty()) {
                        System.out.println("Cliente não possui contas");
                        break;
                    }

                    int i = 1;

                    for (AccountIdentity accountIdentity : accounts) {
                        System.out.printf("%d - %s\n", i++, accountIdentity);
                    }

                    System.out.println("Escolha a conta que você deseja transferir: ");

                    int choice = InputReader.readOption(scanner,
                            c -> c > 0 && c <= accounts.size());

                    Money value =
                            InputReader.readMoney(scanner,
                                    "Digite o valor da transferência: ");

                    try {

                        applicationService.transfer(
                                account,
                                accounts.get(choice - 1),
                                value
                        );

                        System.out.println("Transferência realizada!");

                    } catch (InvalidAmountException |
                             InsufficientBalanceException |
                             InvalidTransferException |
                             AccountNotFoundException e) {

                        System.out.println("Erro: " + e.getMessage());
                    }

                    break;

                case 5:

                    try {

                        List<Transaction> transactionHistory =
                                applicationService.getAccountTransactions(account);

                        if (transactionHistory.isEmpty()) {
                            System.out.println("Conta sem extrato");
                            break;
                        }

                        for (Transaction transaction : transactionHistory) {
                            System.out.println(transaction);
                        }

                    } catch (AccountNotFoundException e) {
                        System.out.println("Erro: " + e.getMessage());
                    }

                    break;

                case 0:
                    return;

                default:
                    System.out.println("Opção inválida");
            }
        }
    }
}