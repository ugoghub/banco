package service;

import exception.AccountDeletionNotAllowedException;
import exception.AccountNotFoundException;
import exception.ClientNotFoundException;
import model.Account;
import model.AccountType;
import model.Client;
import model.valueObjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.AccountRepository;
import repository.ClientRepository;
import repository.TransactionRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AccountServiceTest {

    private ClientService clientService;
    private AccountService accountService;

    private AccountRepository accountRepository;

    @BeforeEach
    void setup() {

        ClientRepository clientRepository = new ClientRepository();
        accountRepository = new AccountRepository();

        clientService =
                new ClientService(clientRepository);

        accountService =
                new AccountService(
                        accountRepository,
                        clientService,
                        Clock.systemUTC()
                );
    }

    @Test
    void shouldCreateAccount() {

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

        Cpf cpf = new Cpf("52998224725");

        clientService.createClient(
                new PersonName("Hugo Silva"),
                cpf,
                new Email("hugo@gmail.com")
        );

        Client client = clientService.getClientByCpf(cpf);

        accountService.createAccount(
                cpf,
                AccountType.CHECKING
        );

        accountService.createAccount(
                cpf,
                AccountType.SAVINGS
        );

        List<AccountIdentity> accounts =
                accountService.getClientAccountsIdentity(client.getId());

        assertEquals(2, accounts.size());
    }

    @Test
    void shouldAssociateAccountWithCorrectClient() {

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

        Client client = clientService.getClientByCpf(cpf1);

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
                accountService.getClientAccountsIdentity(client.getId());

        assertEquals(1, accounts.size());

        assertEquals(
                account1,
                accounts.getFirst()
        );
    }

    @Test
    void shouldThrowExceptionWhenAccountDoesNotExist() {

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

        Cpf cpf =
                new Cpf("52998224725");

        clientService.createClient(
                new PersonName("Hugo Silva"),
                cpf,
                new Email("hugo@gmail.com")
        );

        Client client = clientService.getClientByCpf(cpf);


        List<AccountIdentity> accounts =
                accountService.getClientAccountsIdentity(client.getId());

        assertTrue(accounts.isEmpty());
    }

    @Test
    void shouldApplyPendingInterestWhenGettingBalance() {

        Clock january =
                Clock.fixed(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        ZoneOffset.UTC
                );

        accountService =
                new AccountService(
                        accountRepository,
                        clientService,
                        january
                );

        Cpf cpf =
                new Cpf("52998224725");

        clientService.createClient(
                new PersonName("Hugo Silva"),
                cpf,
                new Email("hugo@gmail.com")
        );

        AccountIdentity identity =
                accountService.createAccount(
                        cpf,
                        AccountType.SAVINGS
                );

        Account account =
                accountService.getAccountByAccountIdentity(identity);

        account.deposit(Money.of("1000"));

        Clock february =
                Clock.fixed(
                        Instant.parse("2026-02-02T10:00:00Z"),
                        ZoneOffset.UTC
                );

        accountService =
                new AccountService(
                        accountRepository,
                        clientService,
                        february
                );

        TransactionService transactionService
                = new TransactionService(
                accountService,
                new TransactionRepository(),
                february
        );

        Money balance =
                transactionService.getAccountBalance(identity);

        assertEquals(
                Money.of("1005"),
                balance
        );
    }

    @Test
    void shouldAllowRemovingClientWhenAllAccountsHaveZeroBalance() {

        Cpf cpf =
                new Cpf("52998224725");

        clientService.createClient(
                new PersonName("Hugo Silva"),
                cpf,
                new Email("hugo@gmail.com")
        );

        Client client =
                clientService.getClientByCpf(cpf);

        accountService.createAccount(
                cpf,
                AccountType.CHECKING
        );

        assertDoesNotThrow(
                () -> accountService
                        .validateIfAccountCanBeRemoved(client.getId())
        );
    }

    @Test
    void shouldDetectNonZeroBalanceAmongManyAccounts() {

        Cpf cpf =
                new Cpf("52998224725");

        clientService.createClient(
                new PersonName("Hugo Silva"),
                cpf,
                new Email("hugo@gmail.com")
        );

        Client client =
                clientService.getClientByCpf(cpf);

        AccountIdentity checking =
                accountService.createAccount(
                        cpf,
                        AccountType.CHECKING
                );

        accountService.createAccount(
                cpf,
                AccountType.SAVINGS
        );

        Account account =
                accountService.getAccountByAccountIdentity(checking);

        account.deposit(Money.of("1"));

        assertThrows(
                AccountDeletionNotAllowedException.class,
                () -> accountService
                        .validateIfAccountCanBeRemoved(client.getId())
        );
    }

    @Test
    void shouldRemoveAllClientAccounts() {

        Cpf cpf =
                new Cpf("52998224725");

        clientService.createClient(
                new PersonName("Hugo Silva"),
                cpf,
                new Email("hugo@gmail.com")
        );

        Client client =
                clientService.getClientByCpf(cpf);

        AccountIdentity first =
                accountService.createAccount(
                        cpf,
                        AccountType.CHECKING
                );

        AccountIdentity second =
                accountService.createAccount(
                        cpf,
                        AccountType.SAVINGS
                );

        accountService.removeClientAccounts(client.getId());

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService
                        .getAccountByAccountIdentity(first)
        );

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService
                        .getAccountByAccountIdentity(second)
        );
    }

    @Test
    void shouldThrowExceptionWhenGettingAccountsFromNonexistentClient() {

        assertThrows(
                ClientNotFoundException.class,
                () -> accountService.getClientAccountsIdentity(
                        UUID.randomUUID()
                )
        );
    }
}
