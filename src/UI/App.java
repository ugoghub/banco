package UI;

import UI.menu.AccountMenu;
import UI.menu.ClientMenu;
import UI.menu.InitialMenu;
import exception.AccountDeletionNotAllowedException;
import exception.ClientNotFoundException;
import exception.CpfAlreadyExistsException;
import exception.EmailAlreadyExistsException;
import model.AccountType;
import model.valueObjects.AccountIdentity;
import model.valueObjects.Cpf;
import model.valueObjects.Email;
import model.valueObjects.PersonName;
import service.ApplicationService;
import service.dto.ClientData;

import java.util.List;
import java.util.Scanner;

public class App {

    private final Scanner scanner;
    private final ApplicationService applicationService;

    private Cpf loggedCpf;

    public App() {
        this.scanner = new Scanner(System.in);
        this.applicationService = new ApplicationService();
    }

    public void start() {

        while (true) {

            InitialMenu.show();

            int option = InputReader.readOption(
                    scanner,
                    o -> o >= 0 && o <= 2
            );

            switch (option) {

                case 1 -> login();

                case 2 -> register();

                case 0 -> {
                    System.out.println("Saindo...");
                    return;
                }
            }
        }
    }


    private void login() {

        Cpf cpf = InputReader.readValidated(
                scanner,
                "CPF: ",
                Cpf::new
        );

        try {

            applicationService.getClientData(cpf);

            loggedCpf = cpf;

            System.out.println("\nBem vindo!");

            enterClientMenu();

        } catch (ClientNotFoundException e) {

            System.out.println(
                    "Erro: " + e.getMessage()
            );
        }
    }

    private void register() {

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

            applicationService.createClient(
                    name,
                    cpf,
                    email
            );

            loggedCpf = cpf;

            System.out.println(
                    "\nCliente cadastrado com sucesso!"
            );

            enterClientMenu();

        } catch (
                CpfAlreadyExistsException |
                EmailAlreadyExistsException e
        ) {

            System.out.println(
                    "Erro: " + e.getMessage()
            );
        }
    }

    private void enterClientMenu() {

        while (loggedCpf != null) {

            ClientData client =
                    applicationService
                            .getClientData(loggedCpf);

            ClientMenu.show(client);

            int option = InputReader.readOption(
                    scanner,
                    o -> o >= 0 && o <= 6
            );

            switch (option) {

                case 1 -> createBankAccount();

                case 2 -> AccountMenu.start(
                        scanner,
                        applicationService,
                        loggedCpf
                );

                case 3 -> showData(client);

                case 4 -> changeData();

                case 5 -> removeBankAccount();

                case 6 -> {

                    removeClient();

                    if (loggedCpf == null) {
                        return;
                    }
                }

                case 0 -> {
                    logout();
                    return;
                }
            }
        }
    }

    private void showData(ClientData client) {

        System.out.println("== DADOS ==\n");

        System.out.printf(
                "Nome: %s\n",
                client.name()
        );

        System.out.printf(
                "Cpf: %s\n",
                client.cpf()
        );

        System.out.printf(
                "Email: %s\n",
                client.email()
        );
    }

    private void changeData() {

        System.out.println(
                "Escolha o campo que você deseja alterar: "
        );

        System.out.println(
                "1 - Nome\n2 - Email"
        );

        int option = InputReader.readOption(
                scanner,
                o -> o >= 1 && o <= 2
        );

        switch (option) {

            case 1 -> {

                PersonName personName =
                        InputReader.readValidated(
                                scanner,
                                "Digite o novo nome: ",
                                PersonName::new
                        );

                applicationService.changeName(
                        loggedCpf,
                        personName
                );
            }

            case 2 -> {

                Email email =
                        InputReader.readValidated(
                                scanner,
                                "Digite o novo email: ",
                                Email::new
                        );

                applicationService.changeEmail(
                        loggedCpf,
                        email
                );
            }
        }
    }

    private void createBankAccount() {

        try {

            System.out.println("""
                    
                    ===== CRIAR CONTA =====
                    1 - Conta Corrente
                    2 - Conta Poupança
                    """);

            System.out.print("Escolha: ");

            int option = InputReader.readOption(
                    scanner,
                    o -> o > 0 && o <= 2
            );

            AccountType type =
                    option == 1
                            ? model.AccountType.CHECKING
                            : model.AccountType.SAVINGS;

            AccountIdentity account = applicationService.createAccount(
                    loggedCpf,
                    type
            );

            System.out.println(
                    "\nConta criada com sucesso!"
            );

            System.out.println(account);

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void removeBankAccount() {

        try {

            List<AccountIdentity> accounts =
                    applicationService.getClientAccountsIdentity(
                            loggedCpf
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

            int option = InputReader.readOption(
                    scanner,
                    o -> o > 0 && o <= accounts.size()
            );

            applicationService.removeAccount(
                    accounts.get(option - 1)
            );

            System.out.println(
                    "Conta removida com sucesso!"
            );

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void removeClient() {

        try {

            applicationService.removeClient(
                    loggedCpf
            );

            System.out.println(
                    "Cliente removido com sucesso!"
            );

            loggedCpf = null;

        } catch (
                AccountDeletionNotAllowedException e
        ) {

            System.out.println(
                    "Erro: " + e.getMessage()
            );
        }
    }

    private void logout() {

        loggedCpf = null;

        System.out.println(
                "\nLogout realizado!"
        );
    }
}