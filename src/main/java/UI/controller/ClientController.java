package UI.controller;

import UI.InputReader;
import UI.messages.ConsoleMessages;
import exception.DomainException;
import model.valueObjects.Cpf;
import model.valueObjects.Email;
import model.valueObjects.PersonName;
import service.ApplicationService;
import service.dto.ClientData;

import java.util.Scanner;

public final class ClientController {
    private final ApplicationService applicationService;
    private final Scanner scanner;

    public ClientController(Scanner scanner, ApplicationService applicationService) {
        this.scanner = scanner;
        this.applicationService = applicationService;
    }

    public void showData(ClientData client) {

        ConsoleMessages.info("== DADOS ==\n");

        ConsoleMessages.info(
                "Nome: %s\n",
                client.name()
        );

        ConsoleMessages.info(
                "Cpf: %s\n",
                client.cpf()
        );

        ConsoleMessages.info(
                "Email: %s\n",
                client.email()
        );
    }

    public void changeData(Cpf loggedCpf) {

        try {

            ConsoleMessages.info(
                    "Escolha o campo que você deseja alterar: "
            );

            ConsoleMessages.info(
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

                    PersonName newName =
                            applicationService.changeName(
                                    loggedCpf,
                                    personName
                            );

                    ConsoleMessages.success(
                            "Nome alterado para %s com sucesso\n",
                            newName
                    );
                }

                case 2 -> {

                    Email email =
                            InputReader.readValidated(
                                    scanner,
                                    "Digite o novo email: ",
                                    Email::new
                            );

                    Email newEmail =
                            applicationService.changeEmail(
                                    loggedCpf,
                                    email
                            );

                    ConsoleMessages.success(
                            "Email alterado para %s com sucesso\n",
                            newEmail
                    );
                }
            }

        } catch (DomainException e) {
            ConsoleMessages.error(e);
        }
    }

    public boolean removeClient(Cpf loggedCpf) {

        try {

            applicationService.removeClient(
                    loggedCpf
            );

            ConsoleMessages.success(
                    "Cliente removido com sucesso!"
            );

            return true;
        } catch (DomainException e) {
            ConsoleMessages.error(e);
        }
        return false;
    }
}
