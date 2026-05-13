package repository;

import model.Client;
import model.valueObject.Cpf;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClientRepository {
    private final Map<Cpf, Client> clients;

    public ClientRepository() {
        this.clients = new HashMap<>();
    }

    public void save(Client client){
        clients.put(client.getCpf(), client);
    }

    public void delete(Cpf cpf){
        clients.remove(cpf);
    }

    public Optional<Client> findByCpf(Cpf cpf){
        return Optional.ofNullable(clients.get(cpf));
    }
}
