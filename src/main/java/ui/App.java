package ui;

import ui.controller.AccountController;
import ui.controller.AccountTransactionController;
import ui.controller.AuthController;
import ui.controller.ClientController;
import ui.menu.ClientMenu;
import ui.menu.InitialMenu;
import ui.messages.ConsoleMessages;
import application.ApplicationContext;
import model.valueobject.Cpf;
import application.ApplicationService;
import service.dto.ClientData;

import java.time.Clock;
import java.util.Scanner;

public final class App {

    private final Scanner scanner;
    private final ApplicationService applicationService;

    private final AuthController authController;
    private final ClientController clientController;
    private final AccountController accountController;

    private Cpf loggedCpf;

    public App() {
        this.scanner = new Scanner(System.in);

        ApplicationContext context =
                new ApplicationContext(Clock.systemUTC());

        applicationService =
                new ApplicationService(
                        context.getClientService(),
                        context.getAccountService(),
                        context.getTransactionService()
                );

        authController =
                new AuthController(
                        scanner,
                        applicationService
                );

        clientController =
                new ClientController(
                        scanner,
                        applicationService
                );

        AccountTransactionController transactionController =
                new AccountTransactionController(scanner, applicationService);

        accountController =
                new AccountController(
                        scanner,
                        applicationService,
                        transactionController
                );


    }

    public void start() {

        while (true) {

            InitialMenu.show();

            int option = InputReader.readOption(
                    scanner,
                    o -> o >= 0 && o <= 2
            );

            switch (option) {

                case 1 -> loggedCpf = authController.login();

                case 2 -> loggedCpf = authController.register();

                case 0 -> {
                    ConsoleMessages.success("Saindo...");
                    scanner.close();
                    return;
                }
            }

            if (loggedCpf != null) {
                enterClientMenu();
            }
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

                case 1 -> accountController.createBankAccount(loggedCpf);

                case 2 -> accountController.enterAccount(loggedCpf);

                case 3 -> clientController.showData(client);

                case 4 -> clientController.changeData(loggedCpf);

                case 5 -> accountController.removeBankAccount(loggedCpf);

                case 6 -> {
                    if (clientController.removeClient(loggedCpf)) {
                        logout();
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

    private void logout() {

        loggedCpf = null;

        ConsoleMessages.success(
                "Logout realizado!"
        );
    }
}