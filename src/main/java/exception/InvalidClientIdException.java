package exception;

public class InvalidClientIdException extends DomainException{
    public InvalidClientIdException(String message) {
        super(message);
    }
}
