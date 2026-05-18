package repository;

import model.Transaction;

import java.util.*;

public class TransactionRepository {
    private final Map<UUID, List<Transaction>> transactions;

    public TransactionRepository() {
        this.transactions = new HashMap<>();
    }

    public void save(UUID account, Transaction transaction){
        transactions
                .computeIfAbsent(account, k -> new ArrayList<>())
                .add(transaction);
    }

    public List<Transaction> getTransactionsByAccountId(UUID id){
        return Collections.unmodifiableList(transactions.getOrDefault(id, Collections.emptyList()));
    }
}