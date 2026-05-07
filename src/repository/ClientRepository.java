package repository;

import exception.ClientNotFoundException;
import model.Client;

import java.util.HashMap;
import java.util.Map;

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

    public Client findById(String cpf) throws ClientNotFoundException {
        Client client = clients.get(cpf);

        if (client == null) {
            throw new ClientNotFoundException("Cliente não encontrado");
        }

        return client;
    }


    public boolean existsByCpf(String cpf) {
        return clients.containsKey(cpf);
    }
}
