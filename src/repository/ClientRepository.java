package repository;

import model.Client;
import model.valueObjects.Cpf;
import model.valueObjects.Email;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClientRepository {
    private final Map<Cpf, Client> clients;
    private final Map<Email, Client> clientsByEmail;

    public ClientRepository() {
        this.clients = new HashMap<>();
        this.clientsByEmail = new HashMap<>();
    }

    public void save(Client client){
        clients.put(client.getCpf(), client);
        clientsByEmail.put(client.getEmail(), client);
    }

    public void delete(Cpf cpf){
        Client client = clients.remove(cpf);

        if(client != null) clientsByEmail.remove(client.getEmail());
    }

    public Optional<Client> findByCpf(Cpf cpf) {
        return findBy(clients, cpf);
    }

    public Optional<Client> findByEmail(Email email){
        return findBy(clientsByEmail, email);
    }

    private <K> Optional<Client> findBy(
            Map<K, Client> map,
            K key
    ){
        return Optional.ofNullable(map.get(key));
    }


}