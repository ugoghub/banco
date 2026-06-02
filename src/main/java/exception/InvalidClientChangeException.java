package exception;

public class InvalidClientChangeException extends DomainException{
    public InvalidClientChangeException(String message) {
        super(message);
    }
}
