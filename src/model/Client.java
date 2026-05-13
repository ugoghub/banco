package model;

import java.util.UUID;

public class Client {
    private final UUID id;
    private String name;
    private final String cpf;
    private String email;

    //CPF como objeto?

    public Client(String name,
                  String cpf,
                  String email) {

        this.id = UUID.randomUUID();
        this.name = name;
        this.cpf = cpf;
        this.email = email;
    }

    /*public UUID getId() {
        return id;
    }*/

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCpf() {
        return cpf;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}