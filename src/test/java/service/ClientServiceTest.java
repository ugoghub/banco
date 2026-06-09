package service;

import exception.ClientNotFoundException;
import exception.CpfAlreadyExistsException;
import exception.EmailAlreadyExistsException;
import exception.InvalidClientChangeException;
import model.valueobject.Cpf;
import model.valueobject.Email;
import model.valueobject.PersonName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.ClientRepository;
import service.dto.ClientData;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ClientServiceTest {

    private ClientService clientService;

    private static final Cpf cpf =
            new Cpf("52998224725");

    private static final PersonName name =
            new PersonName("Pedro Silva");

    private static final Email email =
            new Email("pedro@gmail.com");


    @BeforeEach
    void setup() {
        ClientRepository clientRepository = new ClientRepository();

        clientService =
                new ClientService(clientRepository);
    }

    // =========================
    // General
    // =========================

    @Test
    void shouldReturnClientByEmail() {

        clientService.createClient(
                name,
                cpf,
                email
        );

        Cpf found =
                clientService.getCpfByEmail(
                        email
                );

        assertEquals(
                cpf,
                found
        );
    }

    @Test
    void shouldReturnClientIdByCpf() {

        clientService.createClient(
                name,
                cpf,
                email
        );

        UUID clientId =
                clientService.getClientId(cpf);

        assertNotNull(clientId);
    }

    // =========================
    // Validation
    // =========================

    @Test
    void shouldNotAllowDuplicateCpf() {

        clientService.createClient(name, cpf, email);

        assertThrows(
                CpfAlreadyExistsException.class,
                () -> clientService.createClient(
                        new PersonName("Outro Nome"),
                        cpf,
                        new Email("outro@gmail.com")
                )
        );
    }

    @Test
    void shouldNotAllowDuplicateEmail() {
        clientService.createClient(name, cpf, email);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> clientService.createClient(
                        new PersonName("Maria"),
                        new Cpf("11144477735"),
                        email
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenChangingNameFromNonexistentClient() {

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.changeName(
                        cpf,
                        new PersonName("Novo Nome")
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenChangingEmailFromNonexistentClient() {

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.changeEmail(
                        cpf,
                        new Email("novo@gmail.com")
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentClient() {

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.delete(
                        UUID.randomUUID()
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailDoesNotExist() {

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.getCpfByEmail(
                        new Email("missing@gmail.com")
                )
        );
    }

    // =========================
    // Delete
    // =========================

    @Test
    void shouldDeleteClient() {

        clientService.createClient(
                name,
                cpf,
                email
        );

        UUID clientId = clientService.getClientId(cpf);

        clientService.delete(clientId);

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.getClientId(cpf)
        );
    }

    @Test
    void shouldNotFindDeletedClientByEmail() {

        clientService.createClient(
                name,
                cpf,
                email
        );

        UUID clientId = clientService.getClientId(cpf);

        clientService.delete(clientId);

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.getCpfByEmail(
                        email
                )
        );
    }

    // =========================
    // Update
    // =========================

    @Test
    void shouldChangeName() {

        clientService.createClient(name, cpf, email);

        clientService.changeName(
                cpf,
                new PersonName("Pedro Souza")
        );

        ClientData updatedClient = clientService.getClientData(cpf);

        assertEquals(
                "Pedro Souza",
                updatedClient.name().value()
        );
    }

    @Test
    void shouldChangeEmail() {
        clientService.createClient(name, cpf, email);

        clientService.changeEmail(
                cpf,
                new Email("novo@gmail.com")
        );

        ClientData updatedClient = clientService.getClientData(cpf);

        assertEquals(
                "novo@gmail.com",
                updatedClient.email().value()
        );
    }

    @Test
    void shouldNotAllowChangingNameToSameName() {

        clientService.createClient(
                name,
                cpf,
                email
        );

        assertThrows(
                InvalidClientChangeException.class,
                () -> clientService.changeName(
                        cpf,
                        name
                )
        );
    }

    @Test
    void shouldNotAllowChangingEmailToSameEmail() {

        clientService.createClient(
                name,
                cpf,
                email
        );

        assertThrows(
                InvalidClientChangeException.class,
                () -> clientService.changeEmail(
                        cpf,
                        email
                )
        );
    }

    @Test
    void shouldNotFindClientByOldEmailAfterEmailChange() {

        clientService.createClient(
                name,
                cpf,
                email
        );

        clientService.changeEmail(
                cpf,
                new Email("new@gmail.com")
        );

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.getCpfByEmail(
                        email
                )
        );
    }

    @Test
    void shouldNotAllowChangingEmailToExistingEmail() {
        clientService.createClient(name, cpf, email);

        clientService.createClient(
                new PersonName("Ana Silva"),
                new Cpf("76887934086"),
                new Email("ana@gmail.com")
        );

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> clientService.changeEmail(
                        new Cpf("76887934086"),
                        email
                )
        );
    }
}
