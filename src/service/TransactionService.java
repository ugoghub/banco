package service;

import exception.InvalidAmountException;
import exception.InvalidTransferException;
import model.Account;
import model.Transaction;
import model.TransactionType;
import model.valueObject.AccountIdentity;
import model.valueObject.Money;

public class TransactionService {
    private final AccountService accountService;


    public TransactionService(AccountService accountService) {
        this.accountService = accountService;
    }


    public void deposit(AccountIdentity id,
                        Money value) {


        Account account = accountService.getAccountByAccountIdentity(id);
        account.deposit(value);
        account.addTransaction(new Transaction(TransactionType.DEPOSIT, value, null, account.getAccountIdentity()));
    }


    public void withdraw(AccountIdentity id,
                         Money value) {

        Account account = accountService.getAccountByAccountIdentity(id);
        account.withdraw(value);
        account.addTransaction(new Transaction(TransactionType.WITHDRAW, value, account.getAccountIdentity(), null));
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
            Transaction t = new Transaction(TransactionType.TRANSFER, value, from.getAccountIdentity(), to.getAccountIdentity());
            from.addTransaction(t);
            to.addTransaction(t);
        } catch (InvalidAmountException e) {
            from.deposit(value);
            throw e;
        }
    }
}