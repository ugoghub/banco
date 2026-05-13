package service;

import model.Account;
import model.Transaction;
import model.TransactionType;
import exception.*;
import model.valueObject.Money;


import java.util.UUID;

public class TransactionService {
    private final AccountService accountService;


    public TransactionService(AccountService accountService) {
        this.accountService = accountService;
    }


    public void deposit(UUID id,
                        Money value) {


        Account account = accountService.getAccount(id);
        account.deposit(value);
        account.addTransaction(new Transaction(TransactionType.DEPOSIT, value, null, account.getAccountIdentity()));
    }


    public void withdraw(UUID id,
                         Money value) {

        Account account = accountService.getAccount(id);
        account.withdraw(value);
        account.addTransaction(new Transaction(TransactionType.WITHDRAW, value, account.getAccountIdentity(), null));
    }


    public void transfer(UUID fromId,
                         UUID toId,
                         Money value) {

        Account from = accountService.getAccount(fromId);
        Account to = accountService.getAccount(toId);

        if (from.getId().equals(to.getId())) {
            throw new InvalidTransferException("Não é possível transferir para a mesma conta");
        }

        from.withdraw(value);

        try {
            to.deposit(value);
            Transaction t = new Transaction(TransactionType.TRANSFER, value, from.getAccountIdentity(), to.getAccountIdentity());
            from.addTransaction(t);
            to.addTransaction(t);
        } catch (InvalidAmountException e) {
            from.deposit(value);
            throw e;
        }
    }
}