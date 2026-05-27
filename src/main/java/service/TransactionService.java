package service;

import exception.InvalidTransferException;
import model.Account;
import model.Transaction;
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
    private final Clock clock;

    public TransactionService(AccountService accountService,
                              TransactionRepository transactionRepository,
                              Clock clock) {

        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
        this.clock = clock;
    }

    public void deposit(AccountIdentity id,
                        Money value) {


        Account account = accountService.getAccountByAccountIdentity(id);

        accountService.updateSavingsInterest(account);

        account.deposit(value);

        transactionRepository.save(
                account.getId(),
                Transaction.deposit(
                        account.getAccountIdentity(),
                        value,
                        clock
                ));
    }


    public void withdraw(AccountIdentity id,
                         Money value) {

        Account account = accountService.getAccountByAccountIdentity(id);

        accountService.updateSavingsInterest(account);

        account.withdraw(value);

        transactionRepository.save(
                account.getId(),
                Transaction.withdraw(
                        account.getAccountIdentity(),
                        value,
                        clock
                ));
    }


    public void transfer(AccountIdentity fromId,
                         AccountIdentity toId,
                         Money value) {


        Account from = accountService.getAccountByAccountIdentity(fromId);
        Account to = accountService.getAccountByAccountIdentity(toId);

        if (from.equals(to)) {
            throw new InvalidTransferException("Não é possível transferir para a mesma conta");
        }

        accountService.updateSavingsInterest(from);
        accountService.updateSavingsInterest(to);

        from.withdraw(value);

        to.deposit(value);

        UUID operationId = UUID.randomUUID();

        transactionRepository.save(
                from.getId(),
                Transaction.transferSent(
                        operationId,
                        from.getAccountIdentity(), to.getAccountIdentity(),
                        value, clock
                )
        );

        transactionRepository.save(
                to.getId(),
                Transaction.transferReceived(
                        operationId,
                        from.getAccountIdentity(), to.getAccountIdentity(),
                        value, clock
                )
        );
    }

    public List<StatementData> getTransactionHistory(UUID accountId) {
        List<Transaction> transactionsByAccountId = transactionRepository.getTransactionsByAccountId(accountId);

        return transactionsByAccountId.stream()
                .map(t -> new StatementData(
                        t.getType(),
                        t.getDateTime(),
                        t.getSourceIdentity(),
                        t.getDestinationIdentity(),
                        t.getAmount(),
                        t.getId()
                )).toList();
    }
}