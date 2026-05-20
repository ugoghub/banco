package service;

import exception.InvalidTransferException;
import model.Account;
import model.Transaction;
import model.TransactionType;
import model.valueObjects.AccountIdentity;
import model.valueObjects.Money;
import repository.TransactionRepository;
import service.dto.StatementData;

import java.time.Clock;
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
                        Money value, Clock clock) {


        Account account = accountService.getAccountByAccountIdentity(id);
        account.deposit(value);
        transactionRepository.save(account.getId(), new Transaction(TransactionType.DEPOSIT, value,
                null, account.getAccountIdentity(), clock));
    }


    public void withdraw(AccountIdentity id,
                         Money value, Clock clock) {

        Account account = accountService.getAccountByAccountIdentity(id);
        account.withdraw(value);
       transactionRepository.save(account.getId(), new Transaction(TransactionType.WITHDRAW, value,
               account.getAccountIdentity(), null, clock));
    }


    public void transfer(AccountIdentity fromId,
                         AccountIdentity toId,
                         Money value, Clock clock) {


        Account from = accountService.getAccountByAccountIdentity(fromId);
        Account to = accountService.getAccountByAccountIdentity(toId);

        if (from.getId().equals(to.getId())) {
            throw new InvalidTransferException("Não é possível transferir para a mesma conta");
        }

        from.withdraw(value);
        to.deposit(value);
        transactionRepository.save(from.getId(), new Transaction(TransactionType.TRANSFER_SENT, value,
                from.getAccountIdentity(), null, clock));

        transactionRepository.save(to.getId(), new Transaction(TransactionType.TRANSFER_RECEIVED, value,
                to.getAccountIdentity(), null, clock));

    }

    public List<StatementData> getTransactionHistory(UUID accountId) {
        List<Transaction> transactionsByAccountId = transactionRepository.getTransactionsByAccountId(accountId);

        return transactionsByAccountId.stream()
                .map(t -> new StatementData(
                        t.getType(),
                        t.getDateTime(),
                        t.getSourceId(),
                        t.getDestinationId(),
                        t.getAmount(),
                        t.getId()
                )).toList();
    }
}