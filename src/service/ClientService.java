package service;

import exception.ClientNotFoundException;
import exception.CpfAlreadyExistsException;
import exception.InvalidCpfException;
import model.Client;
import model.valueObject.Cpf;
import repository.ClientRepository;

public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void save(String name,
                     String cpf,
                     String email)
            throws CpfAlreadyExistsException, InvalidCpfException {

        if (clientRepository.findByCpf(cpf).isPresent()) {
            throw new CpfAlreadyExistsException("CPF já cadastrado");
        }

        Client client = new Client(name, new Cpf(cpf), email);

        clientRepository.save(client);
    }


    public Client getClient(String cpf)
            throws ClientNotFoundException {

        return clientRepository.
                findByCpf(cpf).
                orElseThrow(() ->
                        new ClientNotFoundException("Cliente não encontrado"));
    }

    public void delete(String cpf)
            throws ClientNotFoundException {

        Client client = getClient(cpf);

        clientRepository.delete(client.getCpf());
    }
}