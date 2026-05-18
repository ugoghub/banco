package UI;

import UI.menu.AccountMenu;
import UI.menu.ClientMenu;
import UI.menu.InitialMenu;
import model.AccountType;
import model.Client;
import model.valueObjects.AccountIdentity;
import service.ApplicationService;
import model.valueObjects.PersonName;
import model.valueObjects.Email;
import model.valueObjects.Cpf;

import java.util.List;
import java.util.Scanner;

public class App {

    private final Scanner scanner;
    private final ApplicationService applicationService;

    private Client loggedClient;

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

        try {

            Cpf cpf = InputReader.readValidated(
                    scanner,
                    "CPF: ",
                    Cpf::new
            );

            loggedClient = applicationService.getClient(cpf);

            System.out.println(
                    "\nBem vindo, "
                            + loggedClient.getName().value()
                            + "!"
            );

            enterClientMenu(loggedClient);

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void register() {

        try {

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

            loggedClient = applicationService.createClient(name, cpf, email);

            System.out.println(
                    "\nCliente cadastrado com sucesso!"
            );

            enterClientMenu(loggedClient);

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void enterClientMenu(Client client) {

        while (loggedClient != null) {

            ClientMenu.show(client);

            int option = InputReader.readOption(
                    scanner,
                    o -> o >= 0 && o <= 4
            );

            switch (option) {

                case 1 -> createBankAccount();

                case 2 -> AccountMenu.start(
                        scanner,
                        applicationService,
                        client
                );

                case 3 -> removeBankAccount();

                case 4 -> {

                    removeClient();

                    // se removeu cliente, sai do menu
                    if (loggedClient == null) {
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
                    loggedClient.getCpf(),
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
                            loggedClient.getCpf()
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
                    loggedClient.getCpf()
            );

            System.out.println(
                    "Cliente removido com sucesso!"
            );

            loggedClient = null;

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void logout() {

        loggedClient = null;

        System.out.println(
                "\nLogout realizado!"
        );
    }
}