package UI;

import exception.InvalidAmountException;
import exception.InvalidOptionException;
import exception.ValidationException;
import model.valueObjects.Money;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

public class InputReader {
    public static Money readMoney(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            try {
                String input = scanner.nextLine().trim().replace(",", ".");
                return new Money(new BigDecimal(input));

            } catch (NumberFormatException | InvalidAmountException e) {
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
                    throw new InvalidOptionException("Opção Invalida, tente novamente\n");
                }
                return value;
            } catch (NumberFormatException | InvalidOptionException e) {
                System.out.print("Opção inválida, digite novamente\n");
                System.out.println("Escolha: ");
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