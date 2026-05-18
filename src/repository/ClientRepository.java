package repository;

import model.Client;
import model.valueObjects.Cpf;
import model.valueObjects.Email;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClientRepository {
    private final Map<Cpf, Client> clientsByCpf;
    private final Map<Email, Client> clientsByEmail;

    public ClientRepository() {
        this.clientsByCpf = new HashMap<>();
        this.clientsByEmail = new HashMap<>();
    }

    public void save(Client client){
        clientsByCpf.put(client.getCpf(), client);
        clientsByEmail.put(client.getEmail(), client);
    }

    public void delete(Cpf cpf){
        Client client = clientsByCpf.remove(cpf);

        if(client != null) clientsByEmail.remove(client.getEmail());
    }

    public boolean existsByCpf(Cpf cpf){
        return clientsByCpf.containsKey(cpf);
    }

    public boolean existsByEmail(Email email){
        return clientsByEmail.containsKey(email);
    }

    public Optional<Client> findByCpf(Cpf cpf) {
        return findBy(clientsByCpf, cpf);
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