package model.valueObjects;

import exception.InvalidEmailException;

import java.util.Objects;
import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public Email {

        Objects.requireNonNull(value);

        value = value
                .trim()
                .toLowerCase();

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidEmailException("Email inválido");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}