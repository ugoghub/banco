package UI;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.function.Predicate;

public class InputReader {
    public static BigDecimal readMoney(Scanner scanner,  String message) {
        while (true) {
            System.out.print(message);
            try {
                String input = scanner.nextLine().trim().replace(",", ".");
                BigDecimal value = new BigDecimal(input);

                if (value.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.print("Valor deve ser maior que zero. \n");
                    continue;
                }

                return value;

            } catch (NumberFormatException e) {
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

    public static String readCpf(Scanner scanner, String message) {
        System.out.print(message);

        String cpf = scanner.nextLine().trim();

        cpf = cpf.replaceAll("[^0-9]", "");

        return cpf;
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
