package service;

import model.Account;
import model.AccountType;
import model.Client;
import model.Transaction;
import model.valueObjects.*;
import repository.AccountRepository;
import repository.ClientRepository;
import repository.TransactionRepository;

import java.util.List;

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

    public void createClient(PersonName name,
                             Cpf cpf,
                             Email email) {

        clientService.save(name, cpf, email);
    }

    public AccountIdentity createAccount(Cpf cpf,
                                         AccountType type)
    {

        return accountService.save(cpf, type);
    }

    public void removeClient(Cpf cpf) {

        Client client = clientService.getClientByCpf(cpf);

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

    public Money getAccountBalance(AccountIdentity id)
    {

        return accountService.getAccountBalance(id);
    }

    public List<Transaction> getAccountTransactions(AccountIdentity accountIdentity){
        Account accountByIdentity = accountService.getAccountByAccountIdentity(accountIdentity);
        return transactionService.getTransactionHistory(accountByIdentity.getId());
    }
}
