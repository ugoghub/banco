package ui.controller;

import ui.InputReader;
import ui.messages.ConsoleMessages;
import exception.DomainException;
import model.valueobject.Cpf;
import model.valueobject.Email;
import model.valueobject.PersonName;
import application.ApplicationService;
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

        ConsoleMessages.highlight("== DADOS ==");

        ConsoleMessages.highlight(
                "Nome: %s",
                client.name()
        );

        ConsoleMessages.highlight(
                "Cpf: %s",
                client.cpf()
        );

        ConsoleMessages.highlight(
                "Email: %s",
                client.email()
        );
    }

    public void changeData(Cpf loggedCpf) {

        try {

            ConsoleMessages.infoLn(
                    "Escolha o campo que você deseja alterar:"
            );

            ConsoleMessages.infoLn(
                    "1 - Nome 2 - Email"
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
                            "Nome alterado para %s com sucesso",
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
                            "Email alterado para %s com sucesso%n",
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
            return false;
        }
    }
}
