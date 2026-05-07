package service;

import model.*;
import exception.*;
import repository.AccountRepository;
import repository.ClientRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ApplicationService {
    private final ClientService clientService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public ApplicationService() {
        ClientRepository clientRepository = new ClientRepository();
        AccountRepository accountRepository = new AccountRepository();

        this.clientService = new ClientService(clientRepository);
        this.accountService = new AccountService(accountRepository, clientService);
        this.transactionService = new TransactionService(accountService);
    }

    public void createClient(String name, String cpf, String email) throws CpfAlreadyExistsException, InvalidCpfException {
        clientService.save(name, cpf, email);
    }

    public Account createAccount(String cpf, TypeAccount type) throws ClientNotFoundException {
        return accountService.save(cpf, type);
    }

    public void deleteClient(String cpf) throws ClientNotFoundException {
        Client client = clientService.get(cpf);

        accountService.deleteAllAccount(client.getCpf());
        clientService.delete(client.getCpf());
    }

    public void deleteAccount(UUID id) throws ClientNotFoundException {
        accountService.deleteAccount(id);
    }

    public void deposit(UUID id, BigDecimal value) throws InvalidAmountException,
            AccountNotFoundException {
        transactionService.deposit(id, value);
    }

    public void withdraw(UUID id, BigDecimal value) throws InvalidAmountException, InsufficientBalanceException, AccountNotFoundException {
        transactionService.withdraw(id, value);
    }

    public void transfer(UUID fromId, UUID toId, BigDecimal value) throws InvalidAmountException, InsufficientBalanceException,
            InvalidTransferException, AccountNotFoundException {

        transactionService.transfer(fromId, toId, value);
    }

    public List<Account> getAccountsByClient(String id) throws InvalidCpfException, ClientNotFoundException {
        return accountService.getAccountsByClient(id);
    }

    public Account getAccountOfClient(String cpf, UUID accountId) throws AccountNotFoundException, ClientNotFoundException {
        return accountService.getAccountOfClient(cpf, accountId);
    }

    public Client getClient(String cpf) throws ClientNotFoundException { return clientService.get(cpf); }

    public Account getAccount(UUID id) throws AccountNotFoundException { return accountService.get(id); }

    public BigDecimal getAccountBalance(UUID id) throws AccountNotFoundException {
        return accountService.getAccountBalance(id);
    }
}

