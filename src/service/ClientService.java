package service;

import exception.ClientNotFoundException;
import exception.CpfAlreadyExistsException;
import model.Client;
import model.valueObject.Cpf;
import repository.ClientRepository;

public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void save(String name,
                     Cpf cpf,
                     String email) {

        if (clientRepository.findByCpf(cpf).isPresent()) {
            throw new CpfAlreadyExistsException("CPF já cadastrado");
        }

        Client client = new Client(name, cpf, email);

        clientRepository.save(client);
    }


    public Client getClient(Cpf cpf) {

        return clientRepository.
                findByCpf(cpf).
                orElseThrow(() ->
                        new ClientNotFoundException("Cliente não encontrado"));
    }

    public void delete(Cpf cpf) {

        Client client = getClient(cpf);

        clientRepository.delete(client.getCpf());
    }
}