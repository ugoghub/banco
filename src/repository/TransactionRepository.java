package repository;
import model.Transaction;

import java.util.*;

public class TransactionRepository {
    private final Map<UUID, ArrayList<Transaction>> transactions;

    public TransactionRepository() {
        this.transactions = new HashMap<>();
    }

    public void save(UUID account, Transaction transaction){
        ArrayList<Transaction> transactions1 = transactions.get(account);
        transactions1.add(transaction);
        transactions.put(account, transactions1);
    }

    public List<Transaction> getTransactionsByAccountId(UUID id){
        return transactions.get(id);
    }
}