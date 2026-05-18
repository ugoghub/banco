package repository;

import model.*;
import model.valueObject.AccountIdentity;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class AccountRepository {
    private final Map<UUID, Account> accounts;
    private final Map<AccountIdentity, UUID> accountIndex;

    public AccountRepository(){
        this.accounts = new HashMap<>();
        this.accountIndex = new HashMap<>();
    }

    private String generateBranch() {
        int branch = ThreadLocalRandom.current().nextInt(0, 10);

        return String.format("%04d", branch);
    }

    private String generateAccountNumber() {
        String accountNumber = String.format("%06d",
                ThreadLocalRandom.current().nextInt(0, 1_000_000));

        return accountNumber + "-" + generateDigit(accountNumber);
    }

    private int generateDigit(String accountNumber) {
        int sum = 0;

        for(char c : accountNumber.toCharArray()) {
            sum += Character.getNumericValue(c);
        }

        return sum % 10;
    }

    public AccountIdentity generateAccountIdentity() {

        AccountIdentity accountIdentity;

        do {

            accountIdentity = new AccountIdentity(generateBranch(), generateAccountNumber());

        } while(accountExists(accountIdentity));

        return accountIdentity;
    }

    private boolean accountExists(AccountIdentity accountIdentity) {
        return accountIndex.containsKey(accountIdentity);
    }

    public Account save(Account account){
        accountIndex.put(account.getAccountIdentity(), account.getId());
        accounts.put(account.getId(), account);
        return account;
    }

    public List<AccountIdentity> getAccountsByClient(UUID id) {

        return accounts.values()
                .stream()
                .filter(a -> a.getClientId().equals(id))
                .map(Account::getAccountIdentity)
                .toList();
    }

    public Optional<Account> findById(UUID id){
        return Optional.ofNullable(accounts.get(id));
    }

    public Optional<Account> findByAccountIdentity(AccountIdentity accountIdentity){
        UUID uuid = accountIndex.get(accountIdentity);
        return findById(uuid);
    }

    public void removeClientAccount(UUID clientId){
        Account removed = accounts.remove(clientId);
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