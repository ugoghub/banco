package model;

import model.valueObject.Cpf;

import java.util.UUID;

public class Client {
    private final UUID id;
    private String name;
    private final Cpf cpf;
    private String email;

    public Client(String name,
                  Cpf cpf,
                  String email) {

        this.id = UUID.randomUUID();
        this.name = name;
        this.cpf = cpf;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Cpf getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}