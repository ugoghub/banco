package UI;

import exception.ValidationException;
import model.valueObjects.Money;

import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

public final class InputReader {

    private InputReader() {
    }

    public static Money readMoney(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            try {
                String input = scanner.nextLine().trim().replace(",", ".");
                return Money.of(input);

            } catch (NumberFormatException | ValidationException e) {
                System.out.print("Formato inválido. Tente novamente:\n");
            }
        }
    }

    public static int readOption(Scanner scanner, Predicate<Integer> predicate) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                int value = Integer.parseInt(input);

                if(!predicate.test(value)){
                    System.out.println("Opção inválida, digite novamente: ");
                    continue;
                }

                return value;
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido");
                System.out.print("Escolha: ");
            }
        }
    }

    public static <T> T readValidated(
            Scanner scanner,
            String message,
            Function<String, T> parser
    ) {

        while (true) {

            System.out.print(message);

            try {

                String input = scanner.nextLine().trim();

                if (input.isBlank()) {
                    System.out.println("Entrada inválida.\n");
                    continue;
                }

                return parser.apply(input);

            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}