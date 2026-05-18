package service;

import exception.AccountDeletionNotAllowedException;
import exception.AccountNotFoundException;
import model.*;
import model.valueObject.AccountIdentity;
import model.valueObject.Cpf;
import model.valueObject.Money;
import repository.AccountRepository;

import java.util.List;
import java.util.UUID;


public class AccountService {
    private final AccountRepository accountRepository;
    private final ClientService clientService;

    public AccountService(AccountRepository accountRepository,
                          ClientService clientService){

        this.accountRepository = accountRepository;
        this.clientService = clientService;
    }


    public Account save(Cpf cpf,
                        AccountType type) {

        Client client = clientService.getClient(cpf);

        Account account;

        AccountIdentity accountIdentity = accountRepository.generateAccountIdentity();

        if(type == AccountType.CHECKING){
            account = new CheckingAccount(client.getId(),accountIdentity, type);
        }else{
            account = new SavingsAccount(client.getId(), accountIdentity, type);
        }

        return accountRepository.save(account);
    }


    public List<AccountIdentity> getClientAccountsIdentity(Cpf cpf) {
        Client client = clientService.getClient(cpf);

        return accountRepository.getAccountsByClient(client.getId());
    }

    public void removeClientAccounts(UUID clientId){
        accountRepository.removeClientAccounts(clientId);
    }

    public void removeClientAccount(AccountIdentity id) {

        Account account = getAccountByAccountIdentity(id);

        if (!account.accountCanBeRemoved()) {
            throw new AccountDeletionNotAllowedException("Conta não pode ser excluída com saldo diferente de zero");
        }

        accountRepository.removeClientAccount(account.getId());
    }

    public Account getAccountByAccountIdentity(AccountIdentity accountIdentity) {

        return accountRepository.
                findByAccountIdentity(accountIdentity).
                orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));
    }


    public Money getAccountBalance(AccountIdentity id) {

        Account account = getAccountByAccountIdentity(id);
        return account.getBalance();
    }

    public void validateIfAccountCanBeRemoved(Cpf cpf) {
        List<AccountIdentity> clientAccounts = getClientAccountsIdentity(cpf);

        boolean hasNonZeroBalance = clientAccounts
                .stream()
                .anyMatch(a ->
                        !getAccountBalance(a).isZero());

        if(hasNonZeroBalance){
            throw new AccountDeletionNotAllowedException("Cliente possui conta com pendência de saldo");
        }
    }
}