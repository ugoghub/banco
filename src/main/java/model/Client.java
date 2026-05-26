package model;

import exception.InvalidClientChangeException;
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

    public Cpf getCpf() {
        return cpf;
    }

    public UUID getId() {
        return id;
    }

    public PersonName getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public void changeName(PersonName newName) {
        if(this.name.equals(newName)) throw new InvalidClientChangeException("Novo nome é igual ao nome atual");
        if(newName == null) throw new InvalidClientChangeException("Nome não pode ser null");

        this.name = newName;
    }

    public void changeEmail(Email newEmail) {
        if(this.email.equals(newEmail)) throw new InvalidClientChangeException("Novo email é igual ao email atual");
        if(newEmail == null) throw new InvalidClientChangeException("Email não pode ser null");

        this.email = newEmail;
    }
}