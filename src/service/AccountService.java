package service;

import exception.AccountDeletionNotAllowedException;
import exception.AccountNotFoundException;
import exception.InvalidAccountTypeException;
import generator.AccountIdentityGenerator;
import model.*;
import model.valueObjects.AccountIdentity;
import model.valueObjects.Cpf;
import model.valueObjects.Money;
import repository.AccountRepository;

import java.time.Clock;
import java.util.List;
import java.util.UUID;


public class AccountService {
    private final AccountRepository accountRepository;
    private final ClientService clientService;

    private final Clock clock;

    public AccountService(AccountRepository accountRepository,
                          ClientService clientService, Clock clock) {

        this.accountRepository = accountRepository;
        this.clientService = clientService;
        this.clock = clock;
    }


    public AccountIdentity save(Cpf cpf, AccountType type) {

        Client client = clientService.getClientByCpf(cpf);

        Account account;
        AccountIdentity accountIdentity;

        do {

            accountIdentity = AccountIdentityGenerator.generate();

        } while (accountRepository.exists(accountIdentity));

        switch (type){
            case CHECKING -> account = new CheckingAccount(client.getId(), accountIdentity, clock);
            case SAVINGS -> account = new SavingsAccount(client.getId(), accountIdentity, clock);
            default -> throw new InvalidAccountTypeException("Tipo de conta inválido");
        }

        return accountRepository.save(account);
    }

    public List<AccountIdentity> getClientAccountsIdentity(Cpf cpf) {
        Client client = clientService.getClientByCpf(cpf);

        return accountRepository.getAccountsByClient(client.getId());
    }


    public void removeClientAccounts(UUID clientId) {
        accountRepository.removeClientAccounts(clientId);
    }

    public void removeAccount(AccountIdentity id) {

        Account account = getAccountByAccountIdentity(id);

        if (!account.canBeRemoved()) {
            throw new AccountDeletionNotAllowedException("Conta não pode ser excluída com saldo diferente de zero");
        }

        accountRepository.removeAccount(account.getId());
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