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

    public void save(PersonName name,
                     Cpf cpf,
                     Email email) {

        if (clientRepository.findByCpf(cpf).isPresent()) {
            throw new CpfAlreadyExistsException("CPF já cadastrado");
        }

        if (clientRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException("Email já cadastrado");
        }

        Client client = new Client(name, cpf, email);

        clientRepository.save(client);
    }


    public Client getClientByCpf(Cpf cpf) {

        return clientRepository.
                findByCpf(cpf).
                orElseThrow(() ->
                        new ClientNotFoundException("Cliente não encontrado"));
    }

    public Client getClientByEmail(Email email) {

        return clientRepository.
                findByEmail(email).
                orElseThrow(() ->
                        new ClientNotFoundException("Cliente não encontrado"));
    }

    public void delete(Cpf cpf) {

        Client client = getClientByCpf(cpf);

        clientRepository.delete(client.getCpf());
    }
}