package service;

import exception.AccountDeletionNotAllowedException;
import exception.AccountNotFoundException;
import exception.ClientNotFoundException;
import model.Account;
import model.AccountType;
import model.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.AccountRepository;
import repository.ClientRepository;
import repository.TransactionRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AccountServiceTest {

    private ClientService clientService;
    private AccountService accountService;
    private AccountRepository accountRepository;

    private static final Cpf cpf = new Cpf("52998224725");
    private static final PersonName name = new PersonName("Hugo Silva");
    private static final Email email =  new Email("hugo@gmail.com");

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

        clientService.createClient(
                name,
                cpf,
                email
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
                        cpf,
                        AccountType.CHECKING
                )
        );
    }

    @Test
    void shouldReturnAccountByIdentity() {

        clientService.createClient(
                name,
                cpf,
                email
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



        clientService.createClient(
                name,
                cpf,
                email
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

        clientService.createClient(
                name,
                cpf,
                email
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

        Cpf cpf2 =
                new Cpf("76887934086");

        clientService.createClient(
                name,
                cpf,
                email
        );

        clientService.createClient(
                new PersonName("Ana Silva"),
                cpf2,
                new Email("ana@gmail.com")
        );

        AccountIdentity account1 =
                accountService.createAccount(
                        cpf,
                        AccountType.CHECKING
                );

        accountService.createAccount(
                cpf2,
                AccountType.SAVINGS
        );

        List<AccountIdentity> accounts =
                accountService.getClientAccountsIdentity(cpf);

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
                        "000001-1"
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
                        "000001-1"
                );

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.removeAccount(identity)
        );
    }

    @Test
    void shouldReturnEmptyAccountsForNewClient() {

        clientService.createClient(
                name,
                cpf,
                email
        );

        List<AccountIdentity> accounts =
                accountService.getClientAccountsIdentity(cpf);

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

        clientService.createClient(
                name,
                cpf,
                email
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

        clientService.createClient(
                name,
                cpf,
                email
        );

        accountService.createAccount(
                cpf,
                AccountType.CHECKING
        );

        assertDoesNotThrow(
                () -> accountService
                        .validateIfAccountCanBeRemoved(cpf)
        );
    }

    @Test
    void shouldDetectNonZeroBalanceAmongManyAccounts() {

        clientService.createClient(
                name,
                cpf,
                email
        );

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
                        .validateIfAccountCanBeRemoved(cpf)
        );
    }

    @Test
    void shouldRemoveAllClientAccounts() {

        clientService.createClient(
                name,
                cpf,
                email
        );

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

        accountService.removeClientAccounts(cpf);

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
                        cpf
                )
        );
    }
}
