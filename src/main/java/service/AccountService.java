package service;

import exception.AccountDeletionNotAllowedException;
import exception.AccountNotFoundException;
import model.Account;
import model.AccountType;
import model.CheckingAccount;
import model.SavingsAccount;
import model.valueobject.AccountIdentity;
import model.valueobject.AccountIdentityFactory;
import repository.AccountRepository;

import java.time.Clock;
import java.util.List;
import java.util.UUID;


public class AccountService {
    private final AccountRepository accountRepository;

    private final Clock clock;

    public AccountService(AccountRepository accountRepository,
                          Clock clock) {

        this.accountRepository = accountRepository;
        this.clock = clock;
    }


    public AccountIdentity createAccount(UUID clientId, AccountType type) {

        AccountIdentity accountIdentity;

        do {

            accountIdentity = AccountIdentityFactory.generate();

        } while (accountRepository.existsByAccountIdentity(accountIdentity));

        Account account =
                switch (type) {
                    case CHECKING ->
                            new CheckingAccount(
                                    clientId,
                                    accountIdentity,
                                    clock
                            );

                    case SAVINGS ->
                            new SavingsAccount(
                                    clientId,
                                    accountIdentity,
                                    clock
                            );
                };

        accountRepository.save(clientId, account);

        return account.getAccountIdentity();
    }

    public List<AccountIdentity> getClientAccountsIdentity(UUID clientId) {

        return accountRepository
                .getAccountsByClient(clientId)
                .stream()
                .map(Account::getAccountIdentity)
                .toList();
    }

    public Account getAccountByAccountIdentity(AccountIdentity accountIdentity) {

        return accountRepository
                .findByAccountIdentity(accountIdentity)
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));
    }


    public void removeClientAccounts(UUID clientId) {

        accountRepository.removeClientAccounts(clientId);
    }

    public void removeAccount(AccountIdentity accountIdentity) {

        Account account = getAccountByAccountIdentity(accountIdentity);

        if (!account.canBeRemoved()) {
            throw new AccountDeletionNotAllowedException("Conta não pode ser excluída com saldo diferente de zero");
        }

        accountRepository.removeAccount(account.getId());
    }

    public void validateIfAccountsCanBeRemoved(UUID clientId) {

        boolean hasNonZeroBalance =
                accountRepository
                        .getAccountsByClient(clientId)
                        .stream()
                        .anyMatch(a -> !a.canBeRemoved());

        if (hasNonZeroBalance) {
            throw new AccountDeletionNotAllowedException(
                    "Cliente possui conta com pendência de saldo"
            );
        }
    }
}