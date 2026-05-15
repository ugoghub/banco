package model;

import model.valueObject.Cpf;
import model.valueObject.Email;
import model.valueObject.PersonName;

import java.util.UUID;

public class Client {
    private final UUID id;
    private PersonName name;
    private final Cpf cpf;
    private Email email;

    public Client(PersonName name,
                  Cpf cpf,
                  Email email) {

        this.id = UUID.randomUUID();
        this.name = name;
        this.cpf = cpf;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public PersonName getName() {
        return name;
    }
    public void changeName(PersonName newName) {
        this.name = newName;
    }

    public Cpf getCpf() {
        return cpf;
    }

    public Email getEmail() {
        return email;
    }
    public void changeEmail(Email newEmail) {
        this.email = newEmail;
    }
}