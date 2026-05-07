package UI;

import model.*;
import exception.*;
import service.ApplicationService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

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

                    String cpf = InputReader.readCpf(scanner, "CPF: ");

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

                    String cpfAccount = InputReader.readCpf(scanner, "CPF do cliente: ");

                    try {
                        System.out.println("Tipo da conta:");
                        System.out.println("1 - Conta Corrente");
                        System.out.println("2 - Conta Poupança");
                        System.out.print("Escolha: ");

                        int typeOption = InputReader.readOption(scanner, t -> t > 0 && t <= 2);

                        TypeAccount type = (typeOption == 1)
                                ? TypeAccount.CHECKING
                                : TypeAccount.SAVING;

                        Account account = applicationService.createAccount(cpfAccount, type);

                        System.out.println("Conta criada com sucesso! ID: " + account.getId());

                    } catch (ClientNotFoundException e) {
                        System.out.println("Erro: " + e.getMessage());
                    }

                    break;

                // ACESSAR CONTA
                case 3:

                    String cpfClient = InputReader.readCpf(scanner, "Digite seu CPF: ");

                    try {
                        List<Account> accounts = applicationService.getAccountsByClient(cpfClient);

                        if (accounts.isEmpty()) {
                            System.out.println("Cliente não possui contas");
                            break;
                        }

                        int i = 1;

                        for (Account account : accounts) {
                            System.out.printf("%d - %s\n", i++, account);
                        }

                        System.out.println("Escolha o id da conta que você deseja acessar: ");

                        int choice = InputReader.readOption(scanner,
                                c -> c > 0 && c <= accounts.size());

                        Account accountOfClient = accounts.get(choice - 1);

                        accountMenu(scanner, applicationService, accountOfClient.getId());

                    } catch (InvalidCpfException | ClientNotFoundException e) {
                        System.out.println("Erro: " + e.getMessage());
                    }

                    break;

                case 4:
                    String clientCpf = InputReader.readCpf(scanner, "Digite seu Cpf para excluir sua conta: ");
                    try {
                        applicationService.deleteClient(clientCpf);
                    } catch (ClientNotFoundException e) {
                        System.out.println("Erro "+ e.getMessage());
                    }
                    break;

                case 5:
                    String cpfCli = InputReader.readCpf(scanner, "Digite seu Cpf: ");

                    try {
                        List<Account> accounts = applicationService.getAccountsByClient(cpfCli);

                        if (accounts.isEmpty()) {
                            System.out.println("Cliente não possui contas");
                            break;
                        }

                        int i = 1;

                        for (Account account : accounts) {
                            System.out.printf("%d - %s\n", i++, account);
                        }

                        System.out.println("Escolha o id da conta que você deseja excluir: ");

                        int choice = InputReader.readOption(scanner,
                                c -> c > 0 && c <= accounts.size());

                        applicationService.deleteAccount(accounts.get(choice-1).getId());


                    } catch (InvalidCpfException | ClientNotFoundException e) {
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
                                    UUID accountId)
            throws InvalidCpfException, ClientNotFoundException {

        while (true) {

            System.out.println("\n===== CONTA " + accountId + " =====");
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

                    BigDecimal depositValue =
                            InputReader.readMoney(scanner, "Valor: ");

                    try {
                        applicationService.deposit(accountId, depositValue);
                        System.out.println("Depósito realizado!");
                    } catch (InvalidAmountException |
                             AccountNotFoundException e) {

                        System.out.println("Erro: " + e.getMessage());
                    }

                    break;

                case 2:

                    BigDecimal withdrawValue =
                            InputReader.readMoney(scanner, "Valor: ");

                    try {
                        applicationService.withdraw(accountId, withdrawValue);
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
                                                .getAccountBalance(accountId)
                                                .toPlainString());

                    } catch (AccountNotFoundException e) {
                        System.out.println("Erro: " + e.getMessage());
                    }

                    break;

                case 4:

                    String cpfTransfer =
                            InputReader.readCpf(scanner,
                                    "Para quem você deseja transferir? (CPF): ");

                    List<Account> accounts =
                            applicationService.getAccountsByClient(cpfTransfer);

                    if (accounts.isEmpty()) {
                        System.out.println("Cliente não possui contas");
                        break;
                    }

                    int i = 1;

                    for (Account account : accounts) {
                        System.out.printf("%d - %s\n", i++, account);
                    }

                    System.out.println("Escolha a conta que você deseja transferir: ");

                    int choice = InputReader.readOption(scanner,
                            c -> c > 0 && c <= accounts.size());

                    BigDecimal value =
                            InputReader.readMoney(scanner,
                                    "Digite o valor da transferência: ");

                    try {

                        applicationService.transfer(
                                accountId,
                                accounts.get(choice - 1).getId(),
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

                        Account account =
                                applicationService.getAccount(accountId);

                        List<Transaction> transactionHistory =
                                account.getTransactionHistory();

                        if (transactionHistory.isEmpty()) {
                            System.out.println("Conta sem extrato");
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