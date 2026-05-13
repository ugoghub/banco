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

    public List<Account> getAccountsByClient(String cpf) {

        return accounts.values()
                .stream()
                .filter(a -> a.getClientCpf().equals(cpf))
                .toList();
    }

    public Optional<Account> findById(UUID id){
        return Optional.ofNullable(accounts.get(id));
    }

    public void removeClientAccount(UUID id){
        accounts.remove(id);
    }

    public void removeClientAccounts(String cpf) {
        accounts.entrySet()
                .removeIf(entry ->
                        entry.getValue().getClientCpf().equals(cpf));
    }
}
