package service.dto;

import model.valueobject.Cpf;
import model.valueobject.Email;
import model.valueobject.PersonName;

public record ClientData(
        PersonName name,
        Cpf cpf,
        Email email
) {
}
