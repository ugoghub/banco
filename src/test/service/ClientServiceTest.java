package test.service;

import exception.ClientNotFoundException;
import exception.CpfAlreadyExistsException;
import exception.EmailAlreadyExistsException;
import model.Client;
import model.valueObjects.Cpf;
import model.valueObjects.Email;
import model.valueObjects.PersonName;
import org.junit.jupiter.api.Test;
import repository.ClientRepository;
import service.ClientService;

import static org.junit.jupiter.api.Assertions.*;

public class ClientServiceTest {
    @Test
    void shouldNotAllowDuplicateCpf() {

        ClientRepository repository =
                new ClientRepository();

        ClientService service =
                new ClientService(repository);

        Cpf cpf = new Cpf("52998224725");

        service.save(
                new PersonName("Hugo Silva"),
                cpf,
                new Email("hugo@gmail.com")
        );

        assertThrows(
                CpfAlreadyExistsException.class,
                () -> service.save(
                        new PersonName("Outro Nome"),
                        cpf,
                        new Email("outro@gmail.com")
                )
        );
    }

    @Test
    void shouldChangeEmail() {

        ClientRepository repository =
                new ClientRepository();

        ClientService service =
                new ClientService(repository);

        Cpf cpf = new Cpf("52998224725");

        service.save(
                new PersonName("Hugo Silva"),
                cpf,
                new Email("hugo@gmail.com")
        );

        service.changeEmail(
                cpf,
                new Email("novo@gmail.com")
        );

        Client client =
                service.getClientByCpf(cpf);

        assertEquals(
                "novo@gmail.com",
                client.getEmail().value()
        );
    }

    @Test
    void shouldNotAllowDuplicateEmail() {

        ClientRepository repository =
                new ClientRepository();

        ClientService service =
                new ClientService(repository);

        service.save(
                new PersonName("Hugo Silva"),
                new Cpf("52998224725"),
                new Email("hugo@gmail.com")
        );

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> service.save(
                        new PersonName("Maria"),
                        new Cpf("11144477735"),
                        new Email("hugo@gmail.com")
                )
        );
    }

    @Test
    void shouldDeleteClient() {

        ClientRepository repository =
                new ClientRepository();

        ClientService service =
                new ClientService(repository);

        Cpf cpf = new Cpf("52998224725");

        service.save(
                new PersonName("Hugo Silva"),
                cpf,
                new Email("hugo@gmail.com")
        );

        service.delete(cpf);

        assertThrows(
                ClientNotFoundException.class,
                () -> service.getClientByCpf(cpf)
        );
    }

    @Test
    void shouldReindexEmailAfterEmailChange() {

        ClientRepository repository =
                new ClientRepository();

        ClientService service =
                new ClientService(repository);

        Cpf cpf = new Cpf("52998224725");

        service.save(
                new PersonName("Hugo Silva"),
                cpf,
                new Email("old@gmail.com")
        );

        service.changeEmail(
                cpf,
                new Email("new@gmail.com")
        );

        assertFalse(
                repository.existsByEmail(
                        new Email("old@gmail.com")
                )
        );

        assertTrue(
                repository.existsByEmail(
                        new Email("new@gmail.com")
                )
        );
    }
}
