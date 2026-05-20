package model.valueObjects;

import exception.InvalidPersonNameException;

public record PersonName(String value) {

    public PersonName {

        if (value == null) {
            throw new InvalidPersonNameException("Nome não pode ser null");
        }

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