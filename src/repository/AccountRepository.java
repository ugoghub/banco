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

        String branch;
        String accountNumber;

        do {

            branch = generateBranch();
            accountNumber = generateAccountNumber();

        } while(accountExists(new AccountIdentity(branch, accountNumber)));

        return new AccountIdentity(branch, accountNumber);
    }

    private boolean accountExists(AccountIdentity accountIdentity) {
        return accountIndex.containsKey(accountIdentity);
    }

    public Account save(Account account){
        accountIndex.put(account.getAccountIdentity(), account.getId());
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
        Account account = accounts.get(id);
        accounts.remove(id);
        accountIndex.remove(account.getAccountIdentity());
    }

    public void removeClientAccounts(UUID id) {
        Set<UUID> removedAccountIds = new HashSet<>();

        accounts.entrySet().removeIf(entry -> {

            boolean remove =
                    entry.getValue().getClientId().equals(id);

            if (remove) {
                removedAccountIds.add(entry.getKey());
            }

            return remove;
        });

        accountIndex.entrySet().removeIf(entry ->
                removedAccountIds.contains(entry.getValue()));
    }
}
