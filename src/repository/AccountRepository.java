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

    public boolean exists(AccountIdentity accountIdentity) {
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

    public Optional<Account> findById(UUID id){
        return Optional.ofNullable(accounts.get(id));
    }

    public Optional<Account> findByAccountIdentity(AccountIdentity accountIdentity){
        if(!exists(accountIdentity)) return Optional.empty();

        UUID uuid = accountIndex.get(accountIdentity);
        return findById(uuid);
    }

    public void removeAccount(UUID accountId){
        Account removed = accounts.remove(accountId);
        if(removed != null) accountIndex.remove(removed.getAccountIdentity());
    }

    public void removeClientAccounts(UUID clientId) {
        Set<UUID> removedAccountIds = new HashSet<>();

        accounts.entrySet()
                .removeIf(entry -> {

                    boolean remove =
                            entry.getValue().getClientId().equals(clientId);

                    if (remove) {
                        removedAccountIds.add(entry.getKey());
                    }

                    return remove;
                });

        accountIndex.entrySet().removeIf(entry ->
                removedAccountIds.contains(entry.getValue()));
    }
}