package repository;

import model.Transaction;

import java.util.*;

public class TransactionRepository {
    private final Map<UUID, List<Transaction>> transactionsByAccountId;

    public TransactionRepository() {
        this.transactionsByAccountId = new HashMap<>();
    }

    public void save(UUID accountId, Transaction transaction){
        transactionsByAccountId
                .computeIfAbsent(accountId, k -> new ArrayList<>())
                .add(transaction);
    }

    public List<Transaction> findByAccountId(UUID accountId){
        return List.copyOf(
                transactionsByAccountId.getOrDefault(
                        accountId,
                        List.of()
                )
        );
    }
}