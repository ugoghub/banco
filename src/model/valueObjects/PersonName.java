package model.valueObjects;

import exception.InvalidPersonNameException;

import java.util.Objects;

public record PersonName(String value) {
    public PersonName {
        Objects.requireNonNull(value);

        value = value
                .trim();

        if(value.length() < 4 || !value.matches("[A-Za-zÀ-ÿ ]+")) throw new InvalidPersonNameException("Nome deve contar 4 ou mais letras");
    }
}