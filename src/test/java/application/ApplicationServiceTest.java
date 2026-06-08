package application;

import exception.ClientNotFoundException;
import model.AccountType;
import model.valueobject.Cpf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicationServiceTest {
    private ApplicationService applicationService;

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
