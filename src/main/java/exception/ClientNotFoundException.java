package exception;

public class ClientNotFoundException extends DomainException {
    public ClientNotFoundException(String message) {
        super(message);
    }
}
