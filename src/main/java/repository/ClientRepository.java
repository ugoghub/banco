package repository;

import model.Client;
import model.valueobject.Cpf;
import model.valueobject.Email;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ClientRepository {

    private final Map<UUID, Client> clientsById;
    private final Map<Cpf, UUID> clientIdByCpf;
    private final Map<Email, UUID> clientIdByEmail;

    public ClientRepository() {

        this.clientIdByCpf = new HashMap<>();
        this.clientsById = new HashMap<>();
        this.clientIdByEmail = new HashMap<>();
    }

    public void save(Client client) {

        clientsById.put(client.getId(), client);
        clientIdByCpf.put(client.getCpf(), client.getId());
        clientIdByEmail.put(client.getEmail(), client.getId());
    }

    public void delete(UUID clientId) {

        Client client = clientsById.remove(clientId);

        if (client != null) {
            clientIdByEmail.remove(client.getEmail());
            clientIdByCpf.remove(client.getCpf());
        }
    }

    public boolean existsByCpf(Cpf cpf) {
        return clientIdByCpf.containsKey(cpf);
    }

    public boolean existsByEmail(Email email) {
        return clientIdByEmail.containsKey(email);
    }

    public Optional<Client> findByCpf(Cpf cpf) {

        UUID clientId = clientIdByCpf.get(cpf);

        return findByIndexedId(clientId);
    }

    public Optional<Client> findById(UUID id) {
        return Optional.ofNullable(clientsById.get(id));
    }

    public Optional<Client> findByEmail(Email email) {

        UUID clientId = clientIdByEmail.get(email);

        return findByIndexedId(clientId);
    }

    public void reindexEmail(
            Email oldEmail,
            Client client
    ) {

        clientIdByEmail.remove(oldEmail);
        clientIdByEmail.put(client.getEmail(), client.getId());
    }

    private Optional<Client> findByIndexedId(UUID id) {
        if (id == null) {
            return Optional.empty();
        }

        return findById(id);
    }
}