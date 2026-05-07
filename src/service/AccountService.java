package service;

import exception.AccountNotFoundException;
import exception.ClientNotFoundException;
import exception.InvalidCpfException;
import model.*;
import repository.AccountRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


public class AccountService {
    private final AccountRepository accountRepository;
    private final ClientService clientService;

    public AccountService(AccountRepository accountRepository, ClientService clientService){
        this.accountRepository = accountRepository;
        this.clientService = clientService;
    }


    public Account save(String cpf, TypeAccount type) throws ClientNotFoundException {
        Client client = clientService.get(cpf);

        Account account;

        if(type == TypeAccount.CHECKING){
            account = new CheckingAccount(client.getCpf());
        }else{
            account = new SavingsAccount(client.getCpf());
        }

        return accountRepository.save(account);
    }


    public List<Account> getAccountsByClient(String cpf) throws InvalidCpfException, ClientNotFoundException {
        if(cpf == null) throw new InvalidCpfException("CPF inválido");

        Client client = clientService.get(cpf);

        return accountRepository.getAccountsByClient(client.getCpf());
    }


    public Account getAccountOfClient(String cpf, UUID accountId)
            throws AccountNotFoundException, ClientNotFoundException {
        Client client = clientService.get(cpf);

        Account account = accountRepository.findById(accountId);

        if(account == null){
            throw new AccountNotFoundException("Conta não encontrada");
        }

        if (!account.getClientCpf().equals(client.getCpf())) {
            throw new AccountNotFoundException("Conta não pertence ao cliente");
        }

        return account;
    }

    public void deleteAllAccount(String cpf){
        accountRepository.deleteAllAccount(cpf);
    }

    public void deleteAccount(UUID id){
        accountRepository.deleteAccount(id);
    }

    /*public void deleteAccount(UUID id) throws AccountNotFoundException {
        if (accountRepository.findById(id) == null) {
            throw new AccountNotFoundException("Conta não encontrada");
        }
        accountRepository.deleteAccount(id);
    }*/


    public Account get(UUID id) throws AccountNotFoundException {
        Account account = accountRepository.findById(id);

        if (account == null) {
            throw new AccountNotFoundException("Conta não encontrada");
        }

        return account;
    }


    public BigDecimal getAccountBalance(UUID id) throws AccountNotFoundException {
        Account account = get(id);
        return account.getBalance();
    }
}