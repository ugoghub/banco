package repository;

import model.*;

import java.util.*;

public class AccountRepository {
    private final Map<UUID, Account> accounts;

    public AccountRepository(){
        this.accounts = new HashMap<>();
    }

    public Account save(Account account){
        accounts.put(account.getId(), account);
        return account;
    }

    public List<Account> getAccountsByClient(UUID id) {

        return accounts.values()
                .stream()
                .filter(a -> a.getClientId().equals(id))
                .toList();
    }

    public Optional<Account> findById(UUID id){
        return Optional.ofNullable(accounts.get(id));
    }

    public void removeClientAccount(UUID id){
        accounts.remove(id);
    }

    public void removeClientAccounts(UUID id) {
        accounts.entrySet()
                .removeIf(entry ->
                        entry.getValue().getClientId().equals(id));
    }
}
