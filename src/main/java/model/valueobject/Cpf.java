package model.valueobject;

import exception.InvalidCpfException;

public record Cpf(String value) {

    public Cpf {

        if (value == null) {
            throw new InvalidCpfException("CPF não pode ser null");
        }

        if (!value.matches("\\d{11}") &&
                !value.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            throw new InvalidCpfException("CPF inválido");
        }

        value = value.replaceAll("[.-]", "");

        if (!isValidCpf(value)) {
            throw new InvalidCpfException("CPF inválido");
        }

    }

    private static boolean isValidCpf(String cpf) {

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