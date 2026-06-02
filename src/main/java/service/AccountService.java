package service;

import exception.AccountDeletionNotAllowedException;
import exception.AccountNotFoundException;
import exception.InvalidTypeException;
import model.valueobject.AccountIdentityFactory;
import model.*;
import model.valueobject.AccountIdentity;
import model.valueobject.Cpf;
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


    public AccountIdentity createAccount(Cpf cpf, AccountType type) {

        Client client = clientService.getClientByCpf(cpf);

        Account account;
        AccountIdentity accountIdentity;

        do {

            accountIdentity = AccountIdentityFactory.generate();

        } while (accountRepository.existsByAccountIdentity(accountIdentity));

        switch (type){
            case CHECKING -> account = new CheckingAccount(client.getId(), accountIdentity, clock);
            case SAVINGS -> account = new SavingsAccount(client.getId(), accountIdentity, clock);
            default -> throw new InvalidTypeException("Tipo de conta inválido");
        }

        accountRepository.save(client.getId(), account);

        return account.getAccountIdentity();
    }

    public List<AccountIdentity> getClientAccountsIdentity(UUID clientId) {
        clientService.getClientById(clientId);

        return accountRepository
                .getAccountsByClient(clientId)
                .stream()
                .map(Account::getAccountIdentity)
                .toList();
    }


    public void removeClientAccounts(UUID clientId) {
        accountRepository.removeClientAccounts(clientId);
    }

    public void removeAccount(AccountIdentity identity) {

        Account account = getAccountByAccountIdentity(identity);

        if (!account.canBeRemoved()) {
            throw new AccountDeletionNotAllowedException("Conta não pode ser excluída com saldo diferente de zero");
        }

        accountRepository.removeAccount(account.getId());
    }

    public Account getAccountByAccountIdentity(AccountIdentity identity) {

        return accountRepository
                .findByAccountIdentity(identity)
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));
    }

    public void validateIfAccountCanBeRemoved(UUID clientId) {

        boolean hasNonZeroBalance =
                accountRepository
                        .getAccountsByClient(clientId)
                        .stream()
                        .anyMatch(a -> !a.getBalance().isZero());

        if (hasNonZeroBalance) {
            throw new AccountDeletionNotAllowedException(
                    "Cliente possui conta com pendência de saldo"
            );
        }
    }
}