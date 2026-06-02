package application;

import model.AccountType;
import model.valueobject.*;
import service.AccountService;
import service.ClientService;
import service.TransactionService;
import service.dto.ClientData;
import service.dto.StatementData;

import java.util.List;

public class ApplicationService {
    private final ClientService clientService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public ApplicationService(
            ClientService clientService,
            AccountService accountService,
            TransactionService transactionService
    ) {

        this.clientService = clientService;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    // =========================
    // Client
    // =========================

    public void createClient(PersonName name,
                             Cpf cpf,
                             Email email) {

        clientService.createClient(name, cpf, email);
    }

    public void removeClient(Cpf cpf) {
        clientService.validateIfClientExists(cpf);
        accountService.validateIfAccountCanBeRemoved(cpf);
        accountService.removeClientAccounts(cpf);
        clientService.delete(cpf);
    }

    public ClientData getClientData(Cpf cpf) {
        return clientService.getClientData(cpf);
    }

    public PersonName changeName(Cpf cpf, PersonName name) {
        return clientService.changeName(cpf, name);
    }

    public Email changeEmail(Cpf cpf, Email email) {
        return clientService.changeEmail(cpf, email);
    }

    public Cpf getCpfByEmail(Email email) {

        return clientService.getCpfByEmail(email);
    }

    // =========================
    // Account
    // =========================

    public AccountIdentity createAccount(Cpf cpf, AccountType type)
    {

        return accountService.createAccount(cpf, type);
    }

    public void removeAccount(AccountIdentity accountIdentity) {
        accountService.removeAccount(accountIdentity);
    }

    public List<AccountIdentity> getClientAccountsIdentity(Cpf cpf) {

        return accountService.getClientAccountsIdentity(cpf);
    }

    // =========================
    // Transaction
    // =========================

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

    public Money getAccountBalance(AccountIdentity identity)
    {

        return transactionService.getAccountBalance(identity);
    }

    public List<StatementData> getAccountTransactions(AccountIdentity accountIdentity){
        return transactionService.getTransactionHistoryByAccountIdentity(accountIdentity);
    }
}
