package repository;

import model.Transaction;

import java.util.*;

public class TransactionRepository {
    private final Map<UUID, List<Transaction>> transactionsByAccountId;

    public TransactionRepository() {
        this.transactionsByAccountId = new HashMap<>();
    }

    public void save(UUID account, Transaction transaction){
        transactionsByAccountId
                .computeIfAbsent(account, k -> new ArrayList<>())
                .add(transaction);
    }

    public List<Transaction> getTransactionsByAccountId(UUID id){
        return Collections.unmodifiableList(transactionsByAccountId.getOrDefault(id, Collections.emptyList()));
    }
}