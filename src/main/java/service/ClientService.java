package service;

import exception.*;
import model.Client;
import model.valueobject.Cpf;
import model.valueobject.Email;
import model.valueobject.PersonName;
import repository.ClientRepository;
import service.dto.ClientData;

import java.util.UUID;

public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void createClient(
            PersonName name,
            Cpf cpf,
            Email email
    ) {

        validateCpfUniqueness(cpf);
        validateEmailUniqueness(email);

        Client client = new Client(name, cpf, email);

        clientRepository.save(client);
    }

    // =========================
    // GETTERS
    // =========================

    private Client getClientByCpf(Cpf cpf) {

        return clientRepository
                .findByCpf(cpf)
                .orElseThrow(() ->
                        new ClientNotFoundException(
                                "Cliente não encontrado"
                        ));
    }

    public Cpf getCpfByEmail(Email email) {
        return clientRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ClientNotFoundException(
                                "Cliente não encontrado"
                        )
                )
                .getCpf();
    }

    public UUID getClientId(Cpf cpf) {
        return getClientByCpf(cpf).getId();
    }

    public ClientData getClientData(Cpf cpf) {
        Client client = getClientByCpf(cpf);

        return new ClientData(client.getName(), client.getCpf(), client.getEmail());
    }

    // =========================
    // RemoveActions
    // =========================

    public void delete(UUID clientId) {
        if(!clientRepository.existsById(clientId)) throw new ClientNotFoundException("Cliente não encontrado");

        clientRepository.delete(clientId);
    }

    // =========================
    // Update
    // =========================

    public PersonName changeName(
            Cpf cpf,
            PersonName newName
    ) {

        Client client = getClientByCpf(cpf);

        if(client.hasSameName(newName)) throw new InvalidClientChangeException("Novo nome é igual ao nome atual");

        client.changeName(newName);

        return client.getName();
    }

    public Email changeEmail(
            Cpf cpf,
            Email newEmail
    ) {

        Client client = getClientByCpf(cpf);

        if(client.hasSameEmail(newEmail)) throw new InvalidClientChangeException("Novo email é igual ao email atual");

        validateEmailUniqueness(newEmail);

        Email oldEmail = client.getEmail();

        client.changeEmail(newEmail);

        clientRepository.reindexEmail(
                oldEmail,
                client
        );

        return client.getEmail();
    }

    // =========================
    // Validate
    // =========================

    private void validateCpfUniqueness(Cpf cpf) {

        if (clientRepository.existsByCpf(cpf)) {
            throw new CpfAlreadyExistsException(
                    "CPF já cadastrado"
            );
        }
    }

    private void validateEmailUniqueness(Email email) {

        if (clientRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                    "Email já cadastrado"
            );
        }
    }
}