package service;

import exception.AccountDeletionNotAllowedException;
import exception.AccountNotFoundException;
import model.Account;
import model.AccountType;
import model.CheckingAccount;
import model.SavingsAccount;
import model.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.AccountRepository;
import repository.ClientRepository;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AccountServiceTest {

    private ClientService clientService;
    private AccountService accountService;

    private static final Cpf cpf = new Cpf("52998224725");
    private static final PersonName name = new PersonName("Pedro Silva");
    private static final Email email =  new Email("pedro@gmail.com");

    @BeforeEach
    void setup() {

        ClientRepository clientRepository = new ClientRepository();
        AccountRepository accountRepository = new AccountRepository();

        clientService =
                new ClientService(clientRepository);

        accountService =
                new AccountService(
                        accountRepository,
                        Clock.systemUTC()
                );
    }

    // =========================
    // General
    // =========================

    @Test
    void shouldCreateCheckingAccount() {

        UUID clientId = createClient();

        AccountIdentity identity =
                createClientCheckingAccount(clientId);

        Account account =
                accountService.getAccountByAccountIdentity(identity);

        assertInstanceOf(
                CheckingAccount.class,
                account
        );
    }

    @Test
    void shouldCreateSavingsAccount() {

        UUID clientId = createClient();

        AccountIdentity identity =
                createClientSavingsAccount(clientId);

        Account account =
                accountService.getAccountByAccountIdentity(identity);

        assertInstanceOf(
                SavingsAccount.class,
                account
        );
    }

    @Test
    void shouldReturnAccountByIdentity() {

        UUID clientId = createClient();

        AccountIdentity identity =
                createClientSavingsAccount(clientId);

        Account account =
                accountService.getAccountByAccountIdentity(identity);

        assertEquals(identity, account.getAccountIdentity());
    }

    @Test
    void shouldReturnClientAccounts() {

        UUID clientId = createClient();

        createClientCheckingAccount(clientId);

        createClientSavingsAccount(clientId);

        createClientSavingsAccount(clientId);

        createClientCheckingAccount(clientId);

        List<AccountIdentity> accounts =
                accountService.getClientAccountsIdentity(clientId);

        assertEquals(4, accounts.size());
    }

    @Test
    void shouldReturnEmptyAccountsForNewClient() {

        UUID clientId = createClient();

        List<AccountIdentity> accounts =
                accountService.getClientAccountsIdentity(clientId);

        assertTrue(accounts.isEmpty());
    }

    @Test
    void shouldDetectNonZeroBalanceAmongManyAccounts() {

        UUID clientId = createClient();

        AccountIdentity checking =
                createClientSavingsAccount(clientId);

        createClientCheckingAccount(clientId);

        createClientCheckingAccount(clientId);

        Account account =
                accountService.getAccountByAccountIdentity(checking);

        account.deposit(Money.of("1"));

        assertThrows(
                AccountDeletionNotAllowedException.class,
                () -> accountService
                        .ensureClientAccountsCanBeRemoved(clientId)
        );
    }

    // =========================
    // Delete
    // =========================

    @Test
    void shouldRemoveClientAccountsWhenClientHasNoAccounts() {

        UUID clientId = createClient();

        assertDoesNotThrow(
                () -> accountService.removeClientAccounts(clientId)
        );
    }

    @Test
    void shouldNotRemoveAccountWithBalance() {

        UUID clientId = createClient();

        AccountIdentity identity =
                createClientCheckingAccount(clientId);

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

        UUID clientId = createClient();

        AccountIdentity identity =
                createClientSavingsAccount(clientId);

        accountService.removeAccount(identity);

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService
                        .getAccountByAccountIdentity(identity)
        );
    }

    @Test
    void shouldAllowRemovingClientWhenAllAccountsHaveZeroBalance() {

        UUID clientId = createClient();

        createClientSavingsAccount(clientId);

        createClientCheckingAccount(clientId);

        createClientSavingsAccount(clientId);

        assertDoesNotThrow(
                () -> accountService
                        .ensureClientAccountsCanBeRemoved(clientId)
        );
    }

    @Test
    void shouldRemoveAllClientAccounts() {

        UUID clientId = createClient();

        AccountIdentity first =
                createClientCheckingAccount(clientId);

        AccountIdentity second =
                createClientSavingsAccount(clientId);

        accountService.removeClientAccounts(clientId);

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

    // =========================
    // Validation
    // =========================

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

    // =========================
    // Helper
    // =========================


    private UUID createClient() {
        clientService.createClient(
                name,
                cpf,
                email
        );

        return clientService.getClientId(cpf);
    }

    private AccountIdentity createClientCheckingAccount(UUID clientId) {
        return accountService.createAccount(
                clientId,
                AccountType.CHECKING
        );
    }

    private AccountIdentity createClientSavingsAccount(UUID clientId) {
        return accountService.createAccount(
                clientId,
                AccountType.SAVINGS
        );
    }
}
