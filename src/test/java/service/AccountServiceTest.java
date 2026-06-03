package service;

import exception.AccountDeletionNotAllowedException;
import exception.AccountNotFoundException;
import model.Account;
import model.AccountType;
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
    private static final PersonName name = new PersonName("Hugo Silva");
    private static final Email email =  new Email("hugo@gmail.com");

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

    @Test
    void shouldCreateAccount() {

        clientService.createClient(
                name,
                cpf,
                email
        );

        UUID clientId = clientService.getClientId(cpf);

        AccountIdentity account =
                accountService.createAccount(
                        clientId,
                        AccountType.CHECKING
                );

        assertNotNull(account);
    }

    @Test
    void shouldReturnAccountByIdentity() {

        clientService.createClient(
                name,
                cpf,
                email
        );

        UUID clientId = clientService.getClientId(cpf);

        AccountIdentity identity =
                accountService.createAccount(
                        clientId,
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

        UUID clientId = clientService.getClientId(cpf);

        AccountIdentity identity =
                accountService.createAccount(
                        clientId,
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

        UUID clientId = clientService.getClientId(cpf);

        AccountIdentity identity =
                accountService.createAccount(
                        clientId,
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

        UUID clientId = clientService.getClientId(cpf);

        accountService.createAccount(
                clientId,
                AccountType.CHECKING
        );

        accountService.createAccount(
                clientId,
                AccountType.SAVINGS
        );

        accountService.createAccount(
                clientId,
                AccountType.SAVINGS
        );

        accountService.createAccount(
                clientId,
                AccountType.CHECKING
        );

        List<AccountIdentity> accounts =
                accountService.getClientAccountsIdentity(clientId);

        assertEquals(4, accounts.size());
    }

    @Test
    void shouldAssociateAccountWithCorrectClient() {

        clientService.createClient(
                name,
                cpf,
                email
        );

        UUID clientId = clientService.getClientId(cpf);

        Cpf cpf2 =
                new Cpf("76887934086");

        clientService.createClient(
                new PersonName("Ana Silva"),
                cpf2,
                new Email("ana@gmail.com")
        );

        UUID clientId2 = clientService.getClientId(cpf2);

        AccountIdentity account1 =
                accountService.createAccount(
                        clientId,
                        AccountType.CHECKING
                );

        accountService.createAccount(
                clientId2,
                AccountType.SAVINGS
        );

        List<AccountIdentity> accounts =
                accountService.getClientAccountsIdentity(clientId);

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

        UUID clientId = clientService.getClientId(cpf);

        List<AccountIdentity> accounts =
                accountService.getClientAccountsIdentity(clientId);

        assertTrue(accounts.isEmpty());
    }

    @Test
    void shouldAllowRemovingClientWhenAllAccountsHaveZeroBalance() {

        clientService.createClient(
                name,
                cpf,
                email
        );

        UUID clientId = clientService.getClientId(cpf);

        accountService.createAccount(
                clientId,
                AccountType.CHECKING
        );

        accountService.createAccount(
                clientId,
                AccountType.SAVINGS
        );

        accountService.createAccount(
                clientId,
                AccountType.SAVINGS
        );

        assertDoesNotThrow(
                () -> accountService
                        .validateIfAccountsCanBeRemoved(clientId)
        );
    }

    @Test
    void shouldDetectNonZeroBalanceAmongManyAccounts() {

        clientService.createClient(
                name,
                cpf,
                email
        );

        UUID clientId = clientService.getClientId(cpf);

        AccountIdentity checking =
                accountService.createAccount(
                        clientId,
                        AccountType.CHECKING
                );

        accountService.createAccount(
                clientId,
                AccountType.SAVINGS
        );

        accountService.createAccount(
                clientId,
                AccountType.SAVINGS
        );

        Account account =
                accountService.getAccountByAccountIdentity(checking);

        account.deposit(Money.of("1"));

        assertThrows(
                AccountDeletionNotAllowedException.class,
                () -> accountService
                        .validateIfAccountsCanBeRemoved(clientId)
        );
    }

    @Test
    void shouldRemoveAllClientAccounts() {

        clientService.createClient(
                name,
                cpf,
                email
        );

        UUID clientId = clientService.getClientId(cpf);

        AccountIdentity first =
                accountService.createAccount(
                        clientId,
                        AccountType.CHECKING
                );

        AccountIdentity second =
                accountService.createAccount(
                        clientId,
                        AccountType.SAVINGS
                );

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
}
