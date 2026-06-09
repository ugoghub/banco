package application;

import model.AccountType;
import model.valueobject.*;
import service.AccountService;
import service.ClientService;
import service.TransactionService;
import service.dto.ClientData;
import service.dto.StatementData;

import java.util.List;
import java.util.UUID;

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

        UUID clientId = clientService.getClientId(cpf);

        accountService.ensureClientAccountsCanBeRemoved(clientId);
        accountService.removeClientAccounts(clientId);
        clientService.delete(clientId);
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
        UUID clientId = clientService.getClientId(cpf);

        return accountService.createAccount(clientId, type);
    }

    public void removeAccount(AccountIdentity accountIdentity) {
        accountService.removeAccount(accountIdentity);
    }

    public List<AccountIdentity> getClientAccountsIdentity(Cpf cpf) {
        UUID clientId = clientService.getClientId(cpf);

        return accountService.getClientAccountsIdentity(clientId);
    }

    // =========================
    // Transaction
    // =========================

    public void deposit(AccountIdentity accountIdentity,
                        Money value){

        transactionService.deposit(accountIdentity, value);
    }

    public void withdraw(AccountIdentity accountIdentity,
                         Money value) {

        transactionService.withdraw(accountIdentity, value);
    }

    public void transfer(AccountIdentity fromAccountIdentity,
                         AccountIdentity toAccountIdentity,
                         Money value) {

        transactionService.transfer(fromAccountIdentity, toAccountIdentity, value);
    }

    public Money getAccountBalance(AccountIdentity accountIdentity)
    {

        return transactionService.getAccountBalance(accountIdentity);
    }

    public List<StatementData> getAccountTransactions(AccountIdentity accountIdentity){
        return transactionService.getTransactionHistoryByAccountIdentity(accountIdentity);
    }
}
