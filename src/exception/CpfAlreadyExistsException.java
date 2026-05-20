package exception;

public class CpfAlreadyExistsException extends DomainException {
    public CpfAlreadyExistsException(String message) {
        super(message);
    }
}
