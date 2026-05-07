package service;

import model.Account;
import model.Transaction;
import model.TypeTransaction;
import exception.*;


import java.math.BigDecimal;
import java.util.UUID;

public class TransactionService {
    private final AccountService accountService;


    public TransactionService(AccountService accountService) {
        this.accountService = accountService;
    }


    public void deposit(UUID id, BigDecimal value) throws InvalidAmountException,
            AccountNotFoundException {


        Account account = accountService.get(id);
        account.deposit(value);
        account.addTransaction(new Transaction(TypeTransaction.DEPOSIT, value, null, account));
    }


    public void withdraw(UUID id, BigDecimal value) throws InvalidAmountException, InsufficientBalanceException, AccountNotFoundException {


        Account account = accountService.get(id);
        account.withdraw(value);
        account.addTransaction(new Transaction(TypeTransaction.WITHDRAW, value, account, null));
    }




    public void transfer(UUID fromId, UUID toId, BigDecimal value) throws InvalidAmountException , InsufficientBalanceException,
            InvalidTransferException, AccountNotFoundException {

        Account from = accountService.get(fromId);
        Account to = accountService.get(toId);

        if(from.getId().equals(to.getId())) {
            throw new InvalidTransferException("Não é possível transferir para a mesma conta");
        }

        try{
            from.withdraw(value);
            to.deposit(value);
            Transaction t = new Transaction(TypeTransaction.TRANSFER, value, from, to);
            from.addTransaction(t);
            to.addTransaction(t);
        }catch(InvalidAmountException e){
            from.deposit(value);
            throw e;
        }
    }
}