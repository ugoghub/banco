package exception;

public class InvalidNullArgumentException extends DomainException{
    public InvalidNullArgumentException(String message) {
        super(message);
    }
}
