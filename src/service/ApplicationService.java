package service;

import model.Account;
import model.AccountType;
import model.Client;
import model.Transaction;
import model.valueObject.AccountIdentity;
import model.valueObject.Cpf;
import model.valueObject.Money;
import repository.AccountRepository;
import repository.ClientRepository;
import repository.TransactionRepository;

import java.util.List;
import java.util.UUID;

public class ApplicationService {
    private final ClientService clientService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public ApplicationService() {
        ClientRepository clientRepository = new ClientRepository();
        AccountRepository accountRepository = new AccountRepository();
        TransactionRepository transactionRepository = new TransactionRepository();

        this.clientService = new ClientService(clientRepository);
        this.accountService = new AccountService(accountRepository, clientService);
        this.transactionService = new TransactionService(accountService, transactionRepository);
    }

    public void createClient(String name,
                             Cpf cpf,
                             String email) {

        clientService.save(name, cpf, email);
    }

    public Account createAccount(Cpf cpf,
                                 AccountType type)
             {

        return accountService.save(cpf, type);
    }

    public void removeClient(Cpf cpf) {

        Client client = clientService.getClient(cpf);

        accountService.validateIfAccountCanBeRemoved(client.getCpf());
        accountService.removeClientAccounts(client.getId());
        clientService.delete(client.getCpf());
    }

    public void removeClientAccount(AccountIdentity accountIdentity) {
        accountService.removeClientAccount(accountIdentity);
    }

    public void deposit(AccountIdentity id,
                        Money value){

        transactionService.deposit(id, value);
    }

    public void withdraw(AccountIdentity id,
                         Money value) {

        transactionService.withdraw(id, value);
    }

    public void transfer(AccountIdentity fromId,
                         AccountIdentity toId,
                         Money value) {

        transactionService.transfer(fromId, toId, value);
    }

    public List<AccountIdentity> getClientAccountsIdentity(Cpf cpf) {

        return accountService.getClientAccountsIdentity(cpf);
    }

    public Account getClientAccountIdentity(Cpf cpf,
                                      UUID accountId) {

        return accountService.getClientAccount(cpf, accountId);
    }

    public Client getClient(Cpf cpf)
             { return clientService.getClient(cpf); }

    public Account getAccountByIdentity(AccountIdentity accountIdentity)
             { return accountService.getAccountByAccountIdentity(accountIdentity); }

    public Money getAccountBalance(AccountIdentity id)
             {

        return accountService.getAccountBalance(id);
    }

    public List<Transaction> getAccountTransactions(AccountIdentity accountIdentity){
        Account accountByIdentity = getAccountByIdentity(accountIdentity);
        return transactionService.getTransactionHistory(accountByIdentity.getId());
    }
}

