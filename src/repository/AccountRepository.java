package repository;

import model.Account;
import model.valueObjects.AccountIdentity;

import java.util.*;

public class AccountRepository {
    private final Map<UUID, Account> accounts;
    private final Map<AccountIdentity, UUID> accountIndex;

    public AccountRepository(){
        this.accounts = new HashMap<>();
        this.accountIndex = new HashMap<>();
    }

    public boolean existsByAccountIdentity(AccountIdentity accountIdentity) {
        return accountIndex.containsKey(accountIdentity);
    }

    public AccountIdentity save(Account account){
        accountIndex.put(account.getAccountIdentity(), account.getId());
        accounts.put(account.getId(), account);
        return account.getAccountIdentity();
    }

    public List<AccountIdentity> getAccountsByClient(UUID clientId) {

        return accounts.values()
                .stream()
                .filter(a -> a.getClientId().equals(clientId))
                .map(Account::getAccountIdentity)
                .toList();
    }

    public Optional<Account> findById(UUID accountId){
        return Optional.ofNullable(accounts.get(accountId));
    }

    public Optional<Account> findByAccountIdentity(AccountIdentity accountIdentity){
        UUID accountId = accountIndex.get(accountIdentity);

        if (accountId == null) {
            return Optional.empty();
        }

        return findById(accountId);
    }

    public void removeAccount(UUID accountId){
        Account removed = accounts.remove(accountId);
        if(removed != null) accountIndex.remove(removed.getAccountIdentity());
    }

    public void removeClientAccounts(UUID clientId) {
        List<Account> accountsToRemove =
                accounts.values()
                        .stream()
                        .filter(a -> a.getClientId().equals(clientId))
                        .toList();

        accountsToRemove.forEach(account -> {
            accounts.remove(account.getId());
            accountIndex.remove(account.getAccountIdentity());
        });
    }
}