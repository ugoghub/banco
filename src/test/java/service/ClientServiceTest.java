package service;

import exception.ClientNotFoundException;
import exception.CpfAlreadyExistsException;
import exception.EmailAlreadyExistsException;
import model.Client;
import model.valueObjects.Cpf;
import model.valueObjects.Email;
import model.valueObjects.PersonName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.ClientRepository;
import service.ClientService;

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
        clientService.createClient(client.getName(), client.getCpf(), client.getEmail());

        clientService.delete(client.getCpf());

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.getClientByCpf(client.getCpf())
        );
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentClient() {

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.delete(
                        new Cpf("52998224725")
                )
        );
    }

    @Test
    void shouldReindexEmailAfterEmailChange() {

        Client client = createClient();
        clientService.createClient(client.getName(), client.getCpf(), client.getEmail());

        clientService.changeEmail(
                client.getCpf(),
                new Email("new@gmail.com")
        );

        assertFalse(
                clientRepository.existsByEmail(
                        new Email("old@gmail.com")
                )
        );

        assertTrue(
                clientRepository.existsByEmail(
                        new Email("new@gmail.com")
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
