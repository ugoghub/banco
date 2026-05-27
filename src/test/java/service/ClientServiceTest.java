package service;

import exception.ClientNotFoundException;
import exception.CpfAlreadyExistsException;
import exception.EmailAlreadyExistsException;
import exception.InvalidClientChangeException;
import model.Client;
import model.valueObjects.Cpf;
import model.valueObjects.Email;
import model.valueObjects.PersonName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.ClientRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ClientServiceTest {
    ClientRepository clientRepository;
    private ClientService clientService;

    @BeforeEach
    void setup() {
        clientRepository = new ClientRepository();

        clientService =
                new ClientService(clientRepository);
    }

    @Test
    void shouldNotAllowDuplicateCpf() {

        Client client = createClient();
        clientService.createClient(client.getName(), client.getCpf(), client.getEmail());


        assertThrows(
                CpfAlreadyExistsException.class,
                () -> clientService.createClient(
                        new PersonName("Outro Nome"),
                        client.getCpf(),
                        new Email("outro@gmail.com")
                )
        );
    }

    @Test
    void shouldChangeName() {

        Client client = createClient();
        clientService.createClient(client.getName(), client.getCpf(), client.getEmail());

        clientService.changeName(
                client.getCpf(),
                new PersonName("Pedro Souza")
        );

        Client updatedClient = clientService.getClientByCpf(client.getCpf());

        assertEquals(
                "Pedro Souza",
                updatedClient.getName().value()
        );
    }

    @Test
    void shouldThrowExceptionWhenChangingNameFromNonexistentClient() {

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.changeName(
                        new Cpf("52998224725"),
                        new PersonName("Novo Nome")
                )
        );
    }

    @Test
    void shouldChangeEmail() {

        Client client = createClient();
        clientService.createClient(client.getName(), client.getCpf(), client.getEmail());

        clientService.changeEmail(
                client.getCpf(),
                new Email("novo@gmail.com")
        );

        Client updatedClient = clientService.getClientByCpf(client.getCpf());

        assertEquals(
                "novo@gmail.com",
                updatedClient.getEmail().value()
        );
    }

    @Test
    void shouldThrowExceptionWhenChangingEmailFromNonexistentClient() {

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.changeEmail(
                        new Cpf("52998224725"),
                        new Email("novo@gmail.com")
                )
        );
    }

    @Test
    void shouldNotAllowDuplicateEmail() {

        Client client = createClient();
        clientService.createClient(client.getName(), client.getCpf(), client.getEmail());

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> clientService.createClient(
                        new PersonName("Maria"),
                        new Cpf("11144477735"),
                        client.getEmail()
                )
        );
    }

    @Test
    void shouldNotAllowChangingEmailToExistingEmail() {

        Client client = createClient();
        clientService.createClient(client.getName(), client.getCpf(), client.getEmail());

        clientService.createClient(
                new PersonName("Ana Silva"),
                new Cpf("76887934086"),
                new Email("ana@gmail.com")
        );


        assertThrows(
                EmailAlreadyExistsException.class,
                () -> clientService.changeEmail(
                        new Cpf("76887934086"),
                        client.getEmail()
                )
        );
    }

    @Test
    void shouldDeleteClient() {

        Client client = createClient();

        clientService.createClient(
                client.getName(),
                client.getCpf(),
                client.getEmail()
        );

        Client saved =
                clientService.getClientByCpf(client.getCpf());

        clientService.delete(saved.getId());

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.getClientByCpf(client.getCpf())
        );
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentClient() {

        UUID nonExistentId = UUID.randomUUID();

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.delete(
                        nonExistentId
                )
        );
    }

    @Test
    void shouldNotFindClientByOldEmailAfterEmailChange() {

        Client client = createClient();

        clientService.createClient(
                client.getName(),
                client.getCpf(),
                client.getEmail()
        );

        clientService.changeEmail(
                client.getCpf(),
                new Email("new@gmail.com")
        );

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.getClientByEmail(
                        new Email("old@gmail.com")
                )
        );
    }

    @Test
    void shouldNotFindDeletedClientByEmail() {

        Client client = createClient();

        clientService.createClient(
                client.getName(),
                client.getCpf(),
                client.getEmail()
        );

        Client saved =
                clientService.getClientByCpf(client.getCpf());

        clientService.delete(saved.getId());

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.getClientByEmail(
                        client.getEmail()
                )
        );
    }

    @Test
    void shouldReturnClientByEmail() {

        Client client = createClient();

        clientService.createClient(
                client.getName(),
                client.getCpf(),
                client.getEmail()
        );

        Client found =
                clientService.getClientByEmail(
                        client.getEmail()
                );

        assertEquals(
                client.getCpf(),
                found.getCpf()
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailDoesNotExist() {

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.getClientByEmail(
                        new Email("missing@gmail.com")
                )
        );
    }

    @Test
    void shouldNotAllowChangingNameToSameName() {

        Client client = createClient();

        clientService.createClient(
                client.getName(),
                client.getCpf(),
                client.getEmail()
        );

        assertThrows(
                InvalidClientChangeException.class,
                () -> clientService.changeName(
                        client.getCpf(),
                        client.getName()
                )
        );
    }

    @Test
    void shouldNotAllowChangingEmailToSameEmail() {

        Client client = createClient();

        clientService.createClient(
                client.getName(),
                client.getCpf(),
                client.getEmail()
        );

        assertThrows(
                InvalidClientChangeException.class,
                () -> clientService.changeEmail(
                        client.getCpf(),
                        client.getEmail()
                )
        );
    }

    // =========================
    // Helpers
    // =========================

    private Client createClient(){
        return new Client(
                new PersonName("Hugo Silva"),
                new Cpf("52998224725"),
                new Email("old@gmail.com")
        );
    }
}
