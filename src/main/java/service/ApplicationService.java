package service;

import model.Account;
import model.AccountType;
import model.Client;
import model.valueObjects.*;
import repository.AccountRepository;
import repository.ClientRepository;
import repository.TransactionRepository;
import service.dto.ClientData;
import service.dto.StatementData;

import java.time.Clock;
import java.util.List;

public class ApplicationService {
    private final ClientService clientService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public ApplicationService() {
        Clock clock = Clock.systemDefaultZone();

        ClientRepository clientRepository = new ClientRepository();
        AccountRepository accountRepository = new AccountRepository();
        TransactionRepository transactionRepository = new TransactionRepository();

        this.clientService = new ClientService(clientRepository);
        this.accountService = new AccountService(accountRepository, clientService, clock);
        this.transactionService = new TransactionService(accountService, transactionRepository, clock);
    }

    public void createClient(PersonName name,
                             Cpf cpf,
                             Email email) {

        clientService.createClient(name, cpf, email);
    }

    public AccountIdentity createAccount(Cpf cpf, AccountType type)
    {

        return accountService.createAccount(cpf, type);
    }

    public void removeClient(Cpf cpf) {

        Client client = clientService.getClientByCpf(cpf);

        accountService.validateIfAccountCanBeRemoved(client.getId());
        accountService.removeClientAccounts(client.getId());
        clientService.delete(client.getId());
    }

    public void removeAccount(AccountIdentity accountIdentity) {
        accountService.removeAccount(accountIdentity);
    }

    public void deposit(AccountIdentity identity,
                        Money value){

        transactionService.deposit(identity, value);
    }

    public void withdraw(AccountIdentity identity,
                         Money value) {

        transactionService.withdraw(identity, value);
    }

    public void transfer(AccountIdentity fromId,
                         AccountIdentity toId,
                         Money value) {

        transactionService.transfer(fromId, toId, value);
    }

    public List<AccountIdentity> getClientAccountsIdentity(Cpf cpf) {

        Client client = clientService.getClientByCpf(cpf);

        return accountService.getClientAccountsIdentity(client.getId());
    }

    public Money getAccountBalance(AccountIdentity identity)
    {

        return accountService.getAccountBalance(identity);
    }

    public List<StatementData> getAccountTransactions(AccountIdentity accountIdentity){
        Account accountByIdentity = accountService.getAccountByAccountIdentity(accountIdentity);
        return transactionService.getTransactionHistory(accountByIdentity.getId());
    }

    public ClientData getClientData(Cpf cpf) {
        Client client = clientService.getClientByCpf(cpf);

        return new ClientData(client.getName().value(), client.getCpf().value(), client.getEmail().value());
    }

    public Cpf getCpfByEmail(Email email) {
        return clientService
                .getClientByEmail(email)
                .getCpf();
    }

    public PersonName changeName(Cpf cpf, PersonName name) {
        return clientService.changeName(cpf, name);
    }

    public Email changeEmail(Cpf cpf, Email email) {
        return clientService.changeEmail(cpf, email);
    }
}
