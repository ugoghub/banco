package service;

import exception.ClientNotFoundException;
import exception.CpfAlreadyExistsException;
import exception.EmailAlreadyExistsException;
import exception.InvalidClientChangeException;
import model.Client;
import model.valueobject.Cpf;
import model.valueobject.Email;
import model.valueobject.PersonName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.ClientRepository;
import service.dto.ClientData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClientServiceTest {

    private ClientService clientService;

    private static final Cpf cpf =
            new Cpf("52998224725");

    private static final PersonName name =
            new PersonName("Pedro Silva");

    private static final Email email =
            new Email("pedro@gmail.com");

    private static final Client client = new Client(
            name,
            cpf,
            email
    );


    @BeforeEach
    void setup() {
        ClientRepository clientRepository = new ClientRepository();

        clientService =
                new ClientService(clientRepository);
    }

    @Test
    void shouldNotAllowDuplicateCpf() {

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

        clientService.createClient(client.getName(), client.getCpf(), client.getEmail());

        clientService.changeName(
                client.getCpf(),
                new PersonName("Pedro Souza")
        );

        ClientData updatedClient = clientService.getClientData(client.getCpf());

        assertEquals(
                "Pedro Souza",
                updatedClient.name()
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
    void shouldChangeEmail() {
        clientService.createClient(client.getName(), client.getCpf(), client.getEmail());

        clientService.changeEmail(
                client.getCpf(),
                new Email("novo@gmail.com")
        );

        ClientData updatedClient = clientService.getClientData(client.getCpf());

        assertEquals(
                "novo@gmail.com",
                updatedClient.email()
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
    void shouldNotAllowDuplicateEmail() {
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

        clientService.createClient(
                client.getName(),
                client.getCpf(),
                client.getEmail()
        );

        clientService.delete(client.getCpf());

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.getClientId(client.getCpf())
        );
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentClient() {

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.delete(
                        cpf
                )
        );
    }

    @Test
    void shouldNotFindClientByOldEmailAfterEmailChange() {

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
                () -> clientService.getCpfByEmail(
                        email
                )
        );
    }

    @Test
    void shouldNotFindDeletedClientByEmail() {

        clientService.createClient(
                client.getName(),
                client.getCpf(),
                client.getEmail()
        );

        clientService.delete(client.getCpf());

        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.getCpfByEmail(
                        client.getEmail()
                )
        );
    }

    @Test
    void shouldReturnClientByEmail() {

        clientService.createClient(
                client.getName(),
                client.getCpf(),
                client.getEmail()
        );

        Cpf found =
                clientService.getCpfByEmail(
                        client.getEmail()
                );

        assertEquals(
                client.getCpf(),
                found
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

    @Test
    void shouldNotAllowChangingNameToSameName() {

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
}
