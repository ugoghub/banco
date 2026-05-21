package test.service;

import exception.AccountDeletionNotAllowedException;
import exception.AccountNotFoundException;
import exception.ClientNotFoundException;
import model.Account;
import model.AccountType;
import model.valueObjects.*;
import org.junit.jupiter.api.Test;
import repository.AccountRepository;
import repository.ClientRepository;
import service.AccountService;
import service.ClientService;

import java.time.Clock;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AccountServiceTest {
    @Test
    void shouldCreateAccount() {

        ClientRepository clientRepository =
                new ClientRepository();

        AccountRepository accountRepository =
                new AccountRepository();

        ClientService clientService =
                new ClientService(clientRepository);

        AccountService accountService =
                new AccountService(
                        accountRepository,
                        clientService,
                        Clock.systemDefaultZone()
                );

        Cpf cpf = new Cpf("52998224725");

        clientService.createClient(
                new PersonName("Hugo Silva"),
                cpf,
                new Email("hugo@gmail.com")
        );

        AccountIdentity account =
                accountService.createAccount(
                        cpf,
                        AccountType.CHECKING
                );

        assertNotNull(account);
    }

    @Test
    void shouldThrowExceptionWhenClientDoesNotExist() {

        ClientRepository clientRepository =
                new ClientRepository();

        AccountRepository accountRepository =
                new AccountRepository();

        AccountService accountService =
                new AccountService(
                        accountRepository,
                        new ClientService(clientRepository),
                        Clock.systemUTC()
                );

        assertThrows(
                ClientNotFoundException.class,
                () -> accountService.createAccount(
                        new Cpf("52998224725"),
                        AccountType.CHECKING
                )
        );
    }

    @Test
    void shouldReturnAccountByIdentity() {

        ClientRepository clientRepository =
                new ClientRepository();

        AccountRepository accountRepository =
                new AccountRepository();

        ClientService clientService =
                new ClientService(clientRepository);

        AccountService accountService =
                new AccountService(
                        accountRepository,
                        clientService,
                        Clock.systemUTC()
                );

        Cpf cpf = new Cpf("52998224725");

        clientService.createClient(
                new PersonName("Hugo Silva"),
                cpf,
                new Email("hugo@gmail.com")
        );

        AccountIdentity identity =
                accountService.createAccount(
                        cpf,
                        AccountType.CHECKING
                );

        Account account =
                accountService.getAccountByAccountIdentity(identity);

        assertEquals(identity, account.getAccountIdentity());
    }

    @Test
    void shouldNotRemoveAccountWithBalance() {

        ClientRepository clientRepository =
                new ClientRepository();

        AccountRepository accountRepository =
                new AccountRepository();

        ClientService clientService =
                new ClientService(clientRepository);

        AccountService accountService =
                new AccountService(
                        accountRepository,
                        clientService,
                        Clock.systemUTC()
                );

        Cpf cpf = new Cpf("52998224725");

        clientService.createClient(
                new PersonName("Hugo Silva"),
                cpf,
                new Email("hugo@gmail.com")
        );

        AccountIdentity identity =
                accountService.createAccount(
                        cpf,
                        AccountType.CHECKING
                );

        Account account =
                accountService.getAccountByAccountIdentity(identity);

        account.deposit(
                Money.of("100")
        );

        assertThrows(
                AccountDeletionNotAllowedException.class,
                () -> accountService.removeAccount(identity)
        );
    }

    @Test
    void shouldRemoveAccount() {

        ClientRepository clientRepository =
                new ClientRepository();

        AccountRepository accountRepository =
                new AccountRepository();

        ClientService clientService =
                new ClientService(clientRepository);

        AccountService accountService =
                new AccountService(
                        accountRepository,
                        clientService,
                        Clock.systemUTC()
                );

        Cpf cpf = new Cpf("52998224725");

        clientService.createClient(
                new PersonName("Hugo Silva"),
                cpf,
                new Email("hugo@gmail.com")
        );

        AccountIdentity identity =
                accountService.createAccount(
                        cpf,
                        AccountType.CHECKING
                );

        accountService.removeAccount(identity);

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService
                        .getAccountByAccountIdentity(identity)
        );
    }

    @Test
    void shouldReturnClientAccounts() {

        ClientRepository clientRepository =
                new ClientRepository();

        AccountRepository accountRepository =
                new AccountRepository();

        ClientService clientService =
                new ClientService(clientRepository);

        AccountService accountService =
                new AccountService(
                        accountRepository,
                        clientService,
                        Clock.systemUTC()
                );

        Cpf cpf = new Cpf("52998224725");

        clientService.createClient(
                new PersonName("Hugo Silva"),
                cpf,
                new Email("hugo@gmail.com")
        );

        accountService.createAccount(
                cpf,
                AccountType.CHECKING
        );

        accountService.createAccount(
                cpf,
                AccountType.SAVINGS
        );

        List<AccountIdentity> accounts =
                accountService.getClientAccountsIdentity(cpf);

        assertEquals(2, accounts.size());
    }

    @Test
    void shouldAssociateAccountWithCorrectClient() {

        ClientRepository clientRepository =
                new ClientRepository();

        AccountRepository accountRepository =
                new AccountRepository();

        ClientService clientService =
                new ClientService(clientRepository);

        AccountService accountService =
                new AccountService(
                        accountRepository,
                        clientService,
                        Clock.systemUTC()
                );

        Cpf cpf1 =
                new Cpf("52998224725");

        Cpf cpf2 =
                new Cpf("76887934086");

        clientService.createClient(
                new PersonName("Hugo Silva"),
                cpf1,
                new Email("hugo@gmail.com")
        );

        clientService.createClient(
                new PersonName("Ana Silva"),
                cpf2,
                new Email("ana@gmail.com")
        );

        AccountIdentity account1 =
                accountService.createAccount(
                        cpf1,
                        AccountType.CHECKING
                );

        accountService.createAccount(
                cpf2,
                AccountType.SAVINGS
        );

        List<AccountIdentity> accounts =
                accountService.getClientAccountsIdentity(cpf1);

        assertEquals(1, accounts.size());

        assertEquals(
                account1,
                accounts.getFirst()
        );
    }

    @Test
    void shouldThrowExceptionWhenAccountDoesNotExist() {

        ClientRepository clientRepository =
                new ClientRepository();

        AccountRepository accountRepository =
                new AccountRepository();

        ClientService clientService =
                new ClientService(clientRepository);

        AccountService accountService =
                new AccountService(
                        accountRepository,
                        clientService,
                        Clock.systemUTC()
                );

        AccountIdentity identity =
                new AccountIdentity(
                        "01",
                        "999999-9"
                );

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService
                        .getAccountByAccountIdentity(identity)
        );
    }

    @Test
    void shouldThrowExceptionWhenRemovingNonexistentAccount() {

        ClientRepository clientRepository =
                new ClientRepository();

        AccountRepository accountRepository =
                new AccountRepository();

        ClientService clientService =
                new ClientService(clientRepository);

        AccountService accountService =
                new AccountService(
                        accountRepository,
                        clientService,
                        Clock.systemUTC()
                );

        AccountIdentity identity =
                new AccountIdentity(
                        "01",
                        "999999-9"
                );

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.removeAccount(identity)
        );
    }

    @Test
    void shouldReturnEmptyAccountsForNewClient() {

        ClientRepository clientRepository =
                new ClientRepository();

        AccountRepository accountRepository =
                new AccountRepository();

        ClientService clientService =
                new ClientService(clientRepository);

        AccountService accountService =
                new AccountService(
                        accountRepository,
                        clientService,
                        Clock.systemUTC()
                );

        Cpf cpf =
                new Cpf("52998224725");

        clientService.createClient(
                new PersonName("Hugo Silva"),
                cpf,
                new Email("hugo@gmail.com")
        );

        List<AccountIdentity> accounts =
                accountService.getClientAccountsIdentity(cpf);

        assertTrue(accounts.isEmpty());
    }
}
