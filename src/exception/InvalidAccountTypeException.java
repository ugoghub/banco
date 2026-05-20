package exception;

public class InvalidAccountTypeException extends DomainException {
    public InvalidAccountTypeException(String message) {
        super(message);
    }
}