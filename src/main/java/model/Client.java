package model;

import model.valueObjects.Cpf;
import model.valueObjects.Email;
import model.valueObjects.PersonName;

import java.util.Objects;
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
        this.name = newName;
    }

    public void changeEmail(Email newEmail) {
        this.email = newEmail;
    }

    public boolean hasName(PersonName newName) {
        return this.name.equals(newName);
    }

    public boolean hasEmail(Email newEmail) {
        return this.email.equals(newEmail);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
