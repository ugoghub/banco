package service;

import exception.*;
import model.Client;
import model.valueObjects.Cpf;
import model.valueObjects.Email;
import model.valueObjects.PersonName;
import repository.ClientRepository;

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

    public Client getClientByCpf(Cpf cpf) {

        return clientRepository
                .findByCpf(cpf)
                .orElseThrow(() ->
                        new ClientNotFoundException(
                                "Cliente não encontrado"
                        ));
    }

    public Client getClientByEmail(Email email) {
        return clientRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ClientNotFoundException(
                                "Cliente não encontrado"
                        ));
    }

    public Client getClientById(UUID clientId) {
        return clientRepository
                .findById(clientId)
                .orElseThrow(() ->
                        new ClientNotFoundException(
                                "Cliente não encontrado"
                        ));
    }

    public void delete(UUID clientId) {

        Client client = getClientById(clientId);

        clientRepository.delete(client.getId());
    }

    public PersonName changeName(
            Cpf cpf,
            PersonName newName
    ) {

        if(newName == null) throw new InvalidPersonNameException("Nome não pode ser null");

        Client client = getClientByCpf(cpf);

        if(client.getName().equals(newName)) throw new InvalidClientChangeException("Novo nome é igual ao nome atual");

        client.changeName(newName);

        return client.getName();
    }

    public Email changeEmail(
            Cpf cpf,
            Email newEmail
    ) {
        if(newEmail == null) throw new InvalidEmailException("Email não pode ser null");

        Client client = getClientByCpf(cpf);

        if(client.getEmail().equals(newEmail)) throw new InvalidClientChangeException("Novo email é igual ao email atual");

        validateEmailUniqueness(newEmail);

        Email oldEmail = client.getEmail();

        client.changeEmail(newEmail);

        clientRepository.reindexEmail(
                oldEmail,
                client
        );

        return client.getEmail();
    }

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