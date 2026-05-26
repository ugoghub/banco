package repository;

import model.Client;
import model.valueObjects.Cpf;
import model.valueObjects.Email;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ClientRepository {

    private final Map<UUID, Client> clientsById;
    private final Map<Cpf, UUID> clientsByCpf;
    private final Map<Email, UUID> clientsByEmail;

    public ClientRepository() {

        this.clientsByCpf = new HashMap<>();
        this.clientsById = new HashMap<>();
        this.clientsByEmail = new HashMap<>();
    }

    public void save(Client client) {

        clientsById.put(client.getId(), client);
        clientsByCpf.put(client.getCpf(), client.getId());
        clientsByEmail.put(client.getEmail(), client.getId());
    }

    public void delete(UUID clientId) {

        Client client = clientsById.remove(clientId);

        if (client != null) {
            clientsByEmail.remove(client.getEmail());
            clientsByCpf.remove(client.getCpf());
        }
    }

    public boolean existsByCpf(Cpf cpf) {
        return clientsByCpf.containsKey(cpf);
    }

    public boolean existsByEmail(Email email) {
        return clientsByEmail.containsKey(email);
    }

    public Optional<Client> findByCpf(Cpf cpf) {

        UUID clientId = clientsByCpf.get(cpf);

        return findByIndexedId(clientId);
    }

    public Optional<Client> findById(UUID id) {
        return Optional.ofNullable(clientsById.get(id));
    }

    public Optional<Client> findByEmail(Email email) {

        UUID clientId = clientsByEmail.get(email);

        return findByIndexedId(clientId);
    }

    public void reindexEmail(
            Email oldEmail,
            Client client
    ) {

        clientsByEmail.remove(oldEmail);
        clientsByEmail.put(client.getEmail(), client.getId());
    }

    private Optional<Client> findByIndexedId(UUID id) {
        if (id == null) {
            return Optional.empty();
        }

        return findById(id);
    }
}