package application;

import repository.AccountRepository;
import repository.ClientRepository;
import repository.TransactionRepository;
import service.AccountService;
import service.ClientService;
import service.TransactionService;

import java.time.Clock;

public class ApplicationContext {

    private final ClientService clientService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public ApplicationContext() {

        Clock clock = Clock.systemDefaultZone();

        ClientRepository clientRepository =
                new ClientRepository();

        AccountRepository accountRepository =
                new AccountRepository();

        TransactionRepository transactionRepository =
                new TransactionRepository();

        clientService =
                new ClientService(clientRepository);

        accountService =
                new AccountService(
                        accountRepository,
                        clientService,
                        clock
                );

        transactionService =
                new TransactionService(
                        accountService,
                        transactionRepository,
                        clock
                );
    }

    public ClientService getClientService() {
        return clientService;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public TransactionService getTransactionService() {
        return transactionService;
    }
}
