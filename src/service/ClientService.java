package service;

import exception.ClientNotFoundException;
import exception.CpfAlreadyExistsException;
import exception.InvalidCpfException;
import model.Client;
import repository.ClientRepository;

public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void save(String name,
                     String cpf,
                     String email)
            throws InvalidCpfException,
            CpfAlreadyExistsException {

        if (!isCpfValid(cpf)) {
            throw new InvalidCpfException("CPF inválido");
        }

        if (clientRepository.findById(cpf).isPresent()) {
            throw new CpfAlreadyExistsException("CPF já cadastrado");
        }

        Client client = new Client(name, cpf, email);

        clientRepository.save(client);
    }


    public Client getClient(String cpf)
            throws ClientNotFoundException {

        return clientRepository.
                findById(cpf).
                orElseThrow(() ->
                        new ClientNotFoundException("Cliente não encontrado"));
    }

    public void delete(String cpf)
            throws ClientNotFoundException {

        Client client = getClient(cpf);

        clientRepository.delete(client.getCpf());
    }


    private boolean isCpfValid(String cpf) {
        if (cpf == null || cpf.isBlank()) return false;

        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11) return false;

        // Bloqueia CPFs com todos os números iguais
        if (cpf.matches("(\\d)\\1{10}")) return false;

        int sum = 0;

        // 1º dígito
        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * (10 - i);
        }

        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) firstDigit = 0;

        if (firstDigit != (cpf.charAt(9) - '0')) return false;

        // 2º dígito
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (cpf.charAt(i) - '0') * (11 - i);
        }

        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) secondDigit = 0;

        return secondDigit == (cpf.charAt(10) - '0');
    }
}