package ui.controller;

import ui.InputReader;
import ui.messages.ConsoleMessages;
import exception.DomainException;
import model.valueobject.Cpf;
import model.valueobject.Email;
import model.valueobject.PersonName;
import application.ApplicationService;

import java.util.Scanner;

public final class AuthController {

    private final ApplicationService applicationService;
    private final Scanner scanner;

    public AuthController(Scanner scanner, ApplicationService applicationService) {
        this.applicationService = applicationService;
        this.scanner = scanner;
    }

    public Cpf login() {

        ConsoleMessages.info("""
            
            ===== LOGIN =====
            1 - CPF
            2 - Email
            """);

        Cpf loggedCpf = null;

        int option = InputReader.readOption(
                scanner,
                o -> o >= 1 && o <= 2
        );

        try {

            switch (option) {

                case 1 -> {

                    Cpf cpf = InputReader.readValidated(
                            scanner,
                            "CPF: ",
                            Cpf::new
                    );

                    applicationService.getClientData(cpf);

                    loggedCpf = cpf;
                }

                case 2 -> {

                    Email email = InputReader.readValidated(
                            scanner,
                            "Email: ",
                            Email::new
                    );

                    loggedCpf =
                            applicationService
                                    .getCpfByEmail(email);
                }
            }

            return loggedCpf;

        } catch (DomainException e) {
            ConsoleMessages.error(e);
            return null;
        }
    }

    public Cpf register() {

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

            ConsoleMessages.successLn(
                    "Cliente cadastrado com sucesso!"
            );

            return cpf;

        } catch (DomainException e) {
            ConsoleMessages.error(e);
            return null;
        }
    }
}
