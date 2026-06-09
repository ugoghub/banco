package application;

import exception.ClientNotFoundException;
import model.AccountType;
import model.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.dto.ClientData;

import java.time.Clock;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationServiceTest {
    private ApplicationService applicationService;

    private final PersonName name =
            new PersonName("João Silva");

    private final Cpf cpf =
            new Cpf("52998224725");

    private final Email email =
            new Email("joao@email.com");

    @BeforeEach
    void setup() {
        ApplicationContext context =
                new ApplicationContext(Clock.systemUTC());

        applicationService =
                new ApplicationService(
                        context.getClientService(),
                        context.getAccountService(),
                        context.getTransactionService()
                );
    }

    @Test
    void shouldCreateAccountForExistingClient() {

        applicationService.createClient(
                name,
                cpf,
                email
        );

        AccountIdentity account =
                applicationService.createAccount(
                        cpf,
                        AccountType.CHECKING
                );

        assertNotNull(account);
    }

    @Test
    void shouldReturnClientData() {

        applicationService.createClient(
                name,
                cpf,
                email
        );

        ClientData client =
                applicationService.getClientData(cpf);

        assertEquals(name, client.name());
        assertEquals(cpf, client.cpf());
        assertEquals(email, client.email());
    }

    @Test
    void shouldReturnClientAccounts() {

        applicationService.createClient(
                name,
                cpf,
                email
        );

        applicationService.createAccount(cpf,
                AccountType.CHECKING);

        applicationService.createAccount(cpf,
                AccountType.SAVINGS);

        applicationService.createAccount(cpf,
                AccountType.CHECKING);

        applicationService.createAccount(cpf,
                AccountType.SAVINGS);

        List<AccountIdentity> clientAccounts = applicationService.getClientAccountsIdentity(cpf);

        assertEquals(4, clientAccounts.size());
    }

    @Test
    void shouldChangeClientName() {

        applicationService.createClient(
                name,
                cpf,
                email
        );

        PersonName newName =
                new PersonName("Maria Silva");

        PersonName result =
                applicationService.changeName(
                        cpf,
                        newName
                );

        assertEquals(newName, result);
    }

    @Test
    void shouldChangeClientEmail() {

        applicationService.createClient(
                name,
                cpf,
                email
        );

        Email newEmail =
                new Email("novo@email.com");

        Email result =
                applicationService.changeEmail(
                        cpf,
                        newEmail
                );

        assertEquals(newEmail, result);
    }

    @Test
    void shouldDepositAndReturnUpdatedBalance() {

        applicationService.createClient(
                name,
                cpf,
                email
        );

        AccountIdentity account =
                applicationService.createAccount(
                        cpf,
                        AccountType.CHECKING
                );

        applicationService.deposit(
                account,
                Money.of("100")
        );

        Money balance =
                applicationService.getAccountBalance(
                        account
                );

        assertEquals(
                Money.of("100"),
                balance
        );
    }

    @Test
    void shouldTransferBetweenAccounts() {

        applicationService.createClient(
                name,
                cpf,
                email
        );

        AccountIdentity from =
                applicationService.createAccount(
                        cpf,
                        AccountType.CHECKING
                );

        AccountIdentity to =
                applicationService.createAccount(
                        cpf,
                        AccountType.CHECKING
                );

        applicationService.deposit(
                from,
                Money.of("100")
        );

        applicationService.transfer(
                from,
                to,
                Money.of("40")
        );

        assertEquals(
                Money.of("60"),
                applicationService.getAccountBalance(from)
        );

        assertEquals(
                Money.of("40"),
                applicationService.getAccountBalance(to)
        );
    }

    @Test
    void shouldRemoveClientWithoutAccounts() {

        applicationService.createClient(
                name,
                cpf,
                email
        );

        assertDoesNotThrow(
                () -> applicationService.removeClient(cpf)
        );
    }

    // =========================
    // Validation
    // =========================

    @Test
    void shouldThrowExceptionWhenCreatingAccountToNonexistentClient() {
        assertThrows(
                ClientNotFoundException.class,
                () -> applicationService.createAccount(
                        new Cpf("52998224725"),
                        AccountType.CHECKING
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenRemovingNonexistentClient() {
        assertThrows(
                ClientNotFoundException.class,
                () -> applicationService.removeClient(
                        new Cpf("52998224725")
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenGettingAccountsFromNonexistentClient() {

        assertThrows(
                ClientNotFoundException.class,
                () -> applicationService.getClientAccountsIdentity(
                        new Cpf("52998224725")
                )
        );
    }
}
