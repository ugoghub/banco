package UI;

import exception.InvalidAmountException;
import exception.InvalidCpfException;
import model.valueObject.Cpf;
import model.valueObject.Money;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.function.Predicate;

public class InputReader {
    public static Money readMoney(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            try {
                String input = scanner.nextLine().trim().replace(",", ".");
                Money value = new Money(new BigDecimal(input));

                if (Money.isNegativeOrZero(value)) {
                    System.out.print("Valor deve ser maior que zero. \n");
                    continue;
                }

                return value;

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
                    throw new IllegalArgumentException("Opção Invalida, tente novamente\n");
                }
                return value;
            } catch (IllegalArgumentException e) {
                System.out.print("Opção inválida, digite novamente\n");
                System.out.println("Escolha: ");
            }
        }
    }

    public static String readString(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);

            String input = scanner.nextLine().trim();

            if (input.isBlank()) {
                System.out.println("Entrada inválida.\n");
                continue;
            }

            return input;
        }
    }

    public static Cpf readCpf(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);

            try {
                String input = scanner.nextLine().trim();

                if (input.isBlank()) {
                    System.out.println("Entrada inválida.\n");
                    continue;
                }

                return new Cpf(input);

            } catch (InvalidCpfException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static String readEmail(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);

            String email = scanner.nextLine().trim();

            if (email.isBlank()) {
                System.out.println("Email inválido.\n");
                continue;
            }

            if (!email.contains("@") || !email.contains(".com")) {
                System.out.println("Email inválido.\n");
                continue;
            }

            return email;
        }
    }
}
