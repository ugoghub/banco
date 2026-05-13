package model.valueObject;

import exception.InvalidCpfException;

import java.util.Objects;

public record Cpf(String cpf) {
    public Cpf {
        Objects.requireNonNull(cpf);

        cpf = cpf.replaceAll("[^0-9]", "");

        if (!cpfValidator(cpf)) {
            throw new InvalidCpfException("CPF inválido");
        }
    }

    private static boolean cpfValidator(String cpf) {

        if (cpf == null || cpf.isBlank()) return false;

        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11) return false;

        // Bloqueia CPFs com todos os números iguais
        if (cpf.matches("(\\d)\\1{10}")) return false;

        int sum = 0;

        // 1º dígito
        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * (10 - i);
        }

        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) firstDigit = 0;

        if (firstDigit != (cpf.charAt(9) - '0')) return false;

        // 2º dígito
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (cpf.charAt(i) - '0') * (11 - i);
        }

        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) secondDigit = 0;

        return secondDigit == (cpf.charAt(10) - '0');
    }
}
