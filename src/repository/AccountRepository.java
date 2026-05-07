package repository;

import model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public Account findById(UUID id){
        return accounts.get(id);
    }

    public void deleteAccount(UUID id){
        accounts.remove(id);
    }

    public void deleteAllAccount(String cpf) {
        accounts.entrySet()
                .removeIf(entry ->
                        entry.getValue().getClientCpf().equals(cpf));
    }
}
