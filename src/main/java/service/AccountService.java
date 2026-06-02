package service;

import exception.AccountDeletionNotAllowedException;
import exception.AccountNotFoundException;
import exception.InvalidTypeException;
import model.Account;
import model.AccountType;
import model.CheckingAccount;
import model.SavingsAccount;
import model.valueobject.AccountIdentity;
import model.valueobject.AccountIdentityFactory;
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

        UUID clientId = clientService.getClientId(cpf);

        Account account;
        AccountIdentity accountIdentity;

        do {

            accountIdentity = AccountIdentityFactory.generate();

        } while (accountRepository.existsByAccountIdentity(accountIdentity));

        switch (type){
            case CHECKING -> account = new CheckingAccount(clientId, accountIdentity, clock);
            case SAVINGS -> account = new SavingsAccount(clientId, accountIdentity, clock);
            default -> throw new InvalidTypeException("Tipo de conta inválido");
        }

        accountRepository.save(clientId, account);

        return account.getAccountIdentity();
    }

    public List<AccountIdentity> getClientAccountsIdentity(Cpf cpf) {
        UUID clientId = clientService.getClientId(cpf);

        return accountRepository
                .getAccountsByClient(clientId)
                .stream()
                .map(Account::getAccountIdentity)
                .toList();
    }


    public void removeClientAccounts(Cpf cpf) {

        UUID clientId = clientService.getClientId(cpf);
        accountRepository.removeClientAccounts(clientId);
    }

    public void removeAccount(AccountIdentity identity) {

        Account account = getAccountByAccountIdentity(identity);

        if (!account.canBeRemoved()) {
            throw new AccountDeletionNotAllowedException("Conta não pode ser excluída com saldo diferente de zero");
        }

        accountRepository.removeAccount(account.getId());
    }

    public UUID getAccountIdByAccountIdentity(AccountIdentity identity) {

        return accountRepository
                .findByAccountIdentity(identity)
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"))
                .getId();
    }

    public void validateIfAccountCanBeRemoved(Cpf cpf) {

        UUID clientId = clientService.getClientId(cpf);

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

    public Account getAccountByAccountIdentity(AccountIdentity identity) {

        return accountRepository
                .findByAccountIdentity(identity)
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));
    }
}