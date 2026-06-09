package repository;

import model.Account;
import model.valueobject.AccountIdentity;

import java.util.*;

public class AccountRepository {
    private final Map<UUID, Account> accountByAccountId;
    private final Map<AccountIdentity, UUID> accountIndex;
    private final Map<UUID, List<UUID>> accountIdByClientId;

    public AccountRepository(){
        this.accountByAccountId = new HashMap<>();
        this.accountIndex = new HashMap<>();
        this.accountIdByClientId = new HashMap<>();
    }

    // =========================
    // Save
    // =========================

    public void save(UUID clientId, Account account){

        accountIdByClientId
                .computeIfAbsent(clientId, k -> new ArrayList<>())
                .add(account.getId());

        accountIndex.put(
                account.getAccountIdentity(),
                account.getId()
        );

        accountByAccountId.put(
                account.getId(),
                account
        );
    }

    // =========================
    // FindBy
    // =========================

    public Optional<Account> findById(UUID accountId){
        return Optional.ofNullable(accountByAccountId.get(accountId));
    }

    public Optional<Account> findByAccountIdentity(AccountIdentity accountIdentity){
        UUID accountId = accountIndex.get(accountIdentity);

        if (accountId == null) {
            return Optional.empty();
        }

        return findById(accountId);
    }

    // =========================
    // Exists
    // =========================

    public boolean existsByAccountIdentity(AccountIdentity accountIdentity) {
        return accountIndex.containsKey(accountIdentity);
    }

    // =========================
    // GET
    // =========================

    public List<Account> getAccountsByClient(UUID clientId) {

        List<UUID> accountIds =
                accountIdByClientId.getOrDefault(clientId, List.of());

        return accountIds.stream()
                .map(accountByAccountId::get)
                .toList();
    }

    // =========================
    // Delete
    // =========================

    public void removeAccount(UUID accountId){

        Account removed =
                accountByAccountId.remove(accountId);

        if (removed == null) {
            return;
        }

        accountIndex.remove(
                removed.getAccountIdentity()
        );

        List<UUID> clientAccounts =
                accountIdByClientId.get(
                        removed.getClientId()
                );

        if (clientAccounts != null) {

            clientAccounts.remove(accountId);

            if (clientAccounts.isEmpty()) {
                accountIdByClientId.remove(
                        removed.getClientId()
                );
            }
        }
    }

    public void removeClientAccounts(UUID clientId) {

        List<UUID> accountIds =
                accountIdByClientId.remove(clientId);

        if (accountIds == null) {
            return;
        }

        for (UUID accountId : accountIds) {

            Account removed =
                    accountByAccountId.remove(accountId);

            if (removed != null) {
                accountIndex.remove(
                        removed.getAccountIdentity()
                );
            }
        }
    }
}