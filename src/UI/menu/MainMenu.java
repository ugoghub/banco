package UI.menu;

import UI.InputReader;
import UI.selector.AccountSelector;
import exception.*;
import model.AccountType;
import model.valueObjects.*;
import service.ApplicationService;

import java.util.List;
import java.util.Scanner;

public class MainMenu {

    private final Scanner scanner;
    private final ApplicationService applicationService;

    public MainMenu(
            Scanner scanner,
            ApplicationService applicationService
    ) {
        this.scanner = scanner;
        this.applicationService = applicationService;
    }

    public void show() {

        while (true) {

            printMenu();

            int option = InputReader.readOption(
                    scanner,
                    o -> o >= 0 && o <= 2
            );

            switch (option) {
                case 1 -> login();
                case 2 -> createClient();
                case 0 -> {
                    System.out.println("Saindo...");
                    return;
                }
            }
        }
    }

    private void printMenu() {

        System.out.println("\n===== BANKLITE =====");
        System.out.println("== Bem-vindo ==\n\n!");
        System.out.println("1 - Fazer login");
        System.out.println("2 - Criar Conta");
        System.out.println("0 - Sair");
        System.out.print("Escolha: ");
    }

    private void login(){
        System.out.println("1 - CPF\n 2 - Email\n Escolha: ");
        int i = InputReader.readOption(scanner, o -> o >= 0 && o <= 2);

        switch (i){
            case 1:
                InputReader.readValidated(scanner, "Digite seu Cpf: ", Cpf::new);
            case 2:
                InputReader.readValidated(scanner, "Digite seu email: ", Email::new);
            default:
                System.out.println("Opção inválida");
        }
    }

    private void createClient() {

        PersonName name = InputReader.readValidated(
                scanner,
                "Nome completo: ",
                PersonName::new
        );

        Cpf cpf = InputReader.readValidated(
                scanner,
                "CPF: ",
                Cpf::new
        );

        Email email = InputReader.readValidated(
                scanner,
                "Email: ",
                Email::new
        );

        try {

            applicationService.createClient(name, cpf, email);

            System.out.println("Cliente cadastrado com sucesso!");

        } catch (
                CpfAlreadyExistsException |
                EmailAlreadyExistsException e
        ) {

            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void createAccount() {

        Cpf cpf = InputReader.readValidated(
                scanner,
                "CPF do cliente: ",
                Cpf::new
        );

        try {

            System.out.println("1 - Conta Corrente");
            System.out.println("2 - Conta Poupança");
            System.out.print("Escolha: ");

            int option = InputReader.readOption(
                    scanner,
                    o -> o > 0 && o <= 2
            );

            AccountType type = option == 1
                    ? AccountType.CHECKING
                    : AccountType.SAVINGS;

            AccountIdentity account =
                    applicationService.createAccount(cpf, type);

            System.out.println("\nConta criada com sucesso!");
            System.out.println(account);

        } catch (ClientNotFoundException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void accessAccount() {

        Cpf cpf = InputReader.readValidated(
                scanner,
                "Digite seu CPF: ",
                Cpf::new
        );

        try {

            List<AccountIdentity> accounts =
                    applicationService.getClientAccountsIdentity(cpf);

            if (accounts.isEmpty()) {
                System.out.println("Cliente não possui contas");
                return;
            }

            AccountIdentity selected = AccountSelector.select(
                    scanner,
                    accounts,
                    "Escolha a conta: "
            );

            new AccountMenu(scanner, applicationService)
                    .show(selected);

        } catch (ClientNotFoundException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void removeClient() {

        Cpf cpf = InputReader.readValidated(
                scanner,
                "Digite seu CPF: ",
                Cpf::new
        );

        try {

            applicationService.removeClient(cpf);

            System.out.println("Cliente removido!");

        } catch (
                ClientNotFoundException |
                AccountDeletionNotAllowedException e
        ) {

            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void removeAccount() {

        Cpf cpf = InputReader.readValidated(
                scanner,
                "Digite seu CPF: ",
                Cpf::new
        );

        try {

            List<AccountIdentity> accounts =
                    applicationService.getClientAccountsIdentity(cpf);

            if (accounts.isEmpty()) {
                System.out.println("Cliente não possui contas");
                return;
            }

            AccountIdentity selected = AccountSelector.select(
                    scanner,
                    accounts,
                    "Escolha a conta para excluir: "
            );

            applicationService.removeClientAccount(selected);

            System.out.println("Conta removida!");

        } catch (
                ClientNotFoundException |
                AccountDeletionNotAllowedException e
        ) {

            System.out.println("Erro: " + e.getMessage());
        }
    }
}