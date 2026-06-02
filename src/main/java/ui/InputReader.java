package ui;

import ui.messages.ConsoleMessages;
import exception.ValidationException;
import model.valueobject.Money;

import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

public final class InputReader {

    private InputReader() {
    }

    public static Money readMoney(Scanner scanner, String message) {
        while (true) {
            ConsoleMessages.info(message);
            try {
                String input = scanner.nextLine().trim().replace(",", ".");
                return Money.of(input);

            }
            catch (ValidationException e) {
                ConsoleMessages.error(e);
            }
        }
    }

    public static int readOption(Scanner scanner, Predicate<Integer> predicate) {
        while (true) {
            ConsoleMessages.info("Escolha: ");
            try {
                String input = scanner.nextLine().trim();
                int value = Integer.parseInt(input);

                if(!predicate.test(value)){
                    ConsoleMessages.error("Opção inválida, digite novamente: ");
                    continue;
                }

                return value;
            } catch (NumberFormatException e) {
                ConsoleMessages.error("Digite um número válido.");
            }
        }
    }

    public static <T> T readValidated(
            Scanner scanner,
            String message,
            Function<String, T> parser
    ) {

        while (true) {

            ConsoleMessages.info(message);

            try {

                String input = scanner.nextLine().trim();

                if (input.isBlank()) {
                    ConsoleMessages.error("Entrada inválida");
                    continue;
                }

                return parser.apply(input);

            } catch (ValidationException e) {
                ConsoleMessages.error(e);
            }
        }
    }
}