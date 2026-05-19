package service;

import exception.ClientNotFoundException;
import exception.CpfAlreadyExistsException;
import exception.EmailAlreadyExistsException;
import model.Client;
import model.valueObjects.Cpf;
import model.valueObjects.Email;
import model.valueObjects.PersonName;
import repository.ClientRepository;

public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void save(
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

    public void delete(Cpf cpf) {

        Client client = getClientByCpf(cpf);

        clientRepository.delete(client.getCpf());
    }

    public void changeName(
            Cpf cpf,
            PersonName newName
    ) {

        Client client = getClientByCpf(cpf);

        client.changeName(newName);
    }

    public void changeEmail(
            Cpf cpf,
            Email newEmail
    ) {

        validateEmailUniqueness(newEmail);

        Client client = getClientByCpf(cpf);

        Email oldEmail = client.getEmail();

        client.changeEmail(newEmail);

        clientRepository.reindexEmail(
                oldEmail,
                client
        );
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