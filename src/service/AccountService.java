package service;

import exception.AccountDeletionNotAllowedException;
import exception.AccountNotFoundException;
import exception.AccountOwnerShipException;
import exception.InvalidAccountTypeException;
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
                          ClientService clientService) {

        this.accountRepository = accountRepository;
        this.clientService = clientService;
    }


    public AccountIdentity save(Cpf cpf,
                                AccountType type) {

        Client client = clientService.getClientByCpf(cpf);

        Account account;
        AccountIdentity accountIdentity;

        do {

            accountIdentity = AccountIdentity.generate();

        } while (accountExists(accountIdentity));

        switch (type) {
            case AccountType.CHECKING -> account = new CheckingAccount(client.getId(), accountIdentity, type);
            case AccountType.SAVINGS -> account = new SavingsAccount(client.getId(), accountIdentity, type);
            default -> throw new InvalidAccountTypeException("Tipo de conta inválido");
        }

        return accountRepository.save(account);
    }

    private boolean accountExists(AccountIdentity accountIdentity){
        return accountRepository.exists(accountIdentity);
    }

    public List<AccountIdentity> getClientAccountsIdentity(Cpf cpf) {
        Client client = clientService.getClientByCpf(cpf);

        return accountRepository.getAccountsByClient(client.getId());
    }


    public AccountIdentity getClientAccount(Cpf cpf,
                                            AccountIdentity id) {

        Client client = clientService.getClientByCpf(cpf);

        Account account = getAccountByAccountIdentity(id);

        if (!client.getId().equals(account.getClientId())) {
            throw new AccountOwnerShipException("Conta não pertence ao cliente");
        }

        return account.getAccountIdentity();
    }

    public void removeClientAccounts(UUID clientId) {
        accountRepository.removeClientAccounts(clientId);
    }

    public void removeClientAccount(AccountIdentity id) {

        Account account = getAccountByAccountIdentity(id);

        if (!account.canBeRemoved()) {
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

        if (hasNonZeroBalance) {
            throw new AccountDeletionNotAllowedException("Cliente possui conta com pendência de saldo");
        }
    }
}