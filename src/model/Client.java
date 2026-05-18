package model;

import model.valueObjects.Cpf;
import model.valueObjects.Email;
import model.valueObjects.PersonName;

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
        if(name.equals(newName)) return; //nao permite alterar nome para o mesmo nome
        this.name = newName;
    }

    public Cpf getCpf() {
        return cpf;
    }

    public Email getEmail() {
        return email;
    }
    public void changeEmail(Email newEmail) {
        if(email.equals(newEmail)) return; //nao permite alterar email para o mesmo email
        this.email = newEmail;
    }
}