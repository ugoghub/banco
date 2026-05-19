package model.valueObjects;

import exception.InvalidPersonNameException;

import java.util.Objects;

public record PersonName(String value) {

    public PersonName {

        Objects.requireNonNull(value, "Nome não pode ser null");

        value = value.trim();

        if (!value.matches("^[A-Za-zÀ-ÿ' -]{4,}$")) {
            throw new InvalidPersonNameException(
                    "Nome inválido"
            );
        }

    }

    @Override
    public String toString() {
        return value;
    }
}