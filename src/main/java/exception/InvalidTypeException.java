package exception;

public class InvalidTypeException extends DomainException {
    public InvalidTypeException(String message) {
        super(message);
    }
}