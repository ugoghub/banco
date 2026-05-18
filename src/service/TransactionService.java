package service;

import exception.InvalidAmountException;
import exception.InvalidTransferException;
import model.Account;
import model.Transaction;
import model.TransactionType;
import model.valueObjects.AccountIdentity;
import model.valueObjects.Money;
import repository.TransactionRepository;

import java.util.List;
import java.util.UUID;

public class TransactionService {
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountService accountService, TransactionRepository transactionRepository) {
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
    }


    public void deposit(AccountIdentity id,
                        Money value) {


        Account account = accountService.getAccountByAccountIdentity(id);
        transactionRepository.save(account.getId(), account.deposit(value));
    }


    public void withdraw(AccountIdentity id,
                         Money value) {

        Account account = accountService.getAccountByAccountIdentity(id);
        transactionRepository.save(account.getId(), account.withdraw(value));
    }


    public void transfer(AccountIdentity fromId,
                         AccountIdentity toId,
                         Money value) {

        Account from = accountService.getAccountByAccountIdentity(fromId);
        Account to = accountService.getAccountByAccountIdentity(toId);

        if (from.getId().equals(to.getId())) {
            throw new InvalidTransferException("Não é possível transferir para a mesma conta");
        }

        from.withdraw(value);

        try {
            to.deposit(value);
            Transaction transaction = new Transaction(TransactionType.TRANSFER, value, from.getAccountIdentity(), to.getAccountIdentity());
            transactionRepository.save(from.getId(), transaction);
            transactionRepository.save(to.getId(), transaction);
        } catch (InvalidAmountException e) {
            from.deposit(value);
            throw e;
        }
    }

    public List<Transaction> getTransactionHistory(UUID account) {
        return transactionRepository.getTransactionsByAccountId(account);
    }
}