package repository;

import model.Client;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClientRepository {
    private final Map<String, Client> clients;

    public ClientRepository() {
        this.clients = new HashMap<>();
    }

    public void save(Client client){
        clients.put(client.getCpf(), client);
    }

    public void delete(String cpf){
        clients.remove(cpf);
    }

    public Optional<Client> findByCpf(String cpf){
        return Optional.ofNullable(clients.get(cpf));
    }
}
