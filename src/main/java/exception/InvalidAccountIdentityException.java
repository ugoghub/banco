package exception;

public class InvalidAccountIdentityException extends DomainException{
    public InvalidAccountIdentityException(String message) {
        super(message);
    }
}
