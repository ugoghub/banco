package service;

import exception.AccountNotFoundException;
import exception.ClientNotFoundException;
import exception.AccountDeletionNotAllowedException;
import model.*;
import repository.AccountRepository;

import java.math.BigDecimal;
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


    public Account save(String cpf,
                        TypeAccount type)
            throws ClientNotFoundException {

        Client client = clientService.getClient(cpf);

        Account account;

        if(type == TypeAccount.CHECKING){
            account = new CheckingAccount(client.getId());
        }else{
            account = new SavingsAccount(client.getId());
        }

        return accountRepository.save(account);
    }


    public List<Account> getClientAccounts(String cpf) throws ClientNotFoundException {
        Client client = clientService.getClient(cpf);

        return accountRepository.getAccountsByClient(client.getId());
    }


    public Account getClientAccount(String cpf,
                                      UUID id)
            throws AccountNotFoundException, ClientNotFoundException {

        Client client = clientService.getClient(cpf);

        Account account = getAccount(id);

        if(!client.getId().equals(account.getClientId())){
            throw new AccountNotFoundException("Conta não pertence ao cliente");
        }

        return account;
    }

    public void removeClientAccounts(UUID id){
        accountRepository.removeClientAccounts(id);
    }

    public void removeClientAccount(UUID id)
            throws AccountNotFoundException, AccountDeletionNotAllowedException {

        Account account = getAccount(id);

        if (!account.accountCanBeRemoved()) {
            throw new AccountDeletionNotAllowedException("Conta não pode ser excluída com saldo diferente de zero");
        }

        accountRepository.removeClientAccount(account.getId());
    }


    public Account getAccount(UUID id)
            throws AccountNotFoundException {

        return accountRepository.
                findById(id).
                orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));
    }


    public BigDecimal getAccountBalance(UUID id)
            throws AccountNotFoundException {

        Account account = getAccount(id);
        return account.getBalance();
    }

    public void validateIfAccountCanBeRemoved(String cpf) throws ClientNotFoundException, AccountDeletionNotAllowedException {
        List<Account> clientAccounts = getClientAccounts(cpf);

        boolean hasNonZeroBalance =
                clientAccounts.stream()
                        .anyMatch(a -> a.getBalance().compareTo(BigDecimal.ZERO) != 0);

        if(hasNonZeroBalance){
            throw new AccountDeletionNotAllowedException("Cliente possui conta com pendência de saldo");
        }
    }
}