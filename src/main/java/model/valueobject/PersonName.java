package model.valueobject;

import exception.InvalidPersonNameException;

public record PersonName(String value) {

    public PersonName {

        if (value == null) {
            throw new InvalidPersonNameException("Nome não pode ser null");
        }

        value = value
                .trim()
                .replaceAll("\\s+", " ");

        if (!value.matches("^[\\p{L}' -]{2,}$")) {
            throw new InvalidPersonNameException(
                    "Nome inválido"
            );
        }
    }
}