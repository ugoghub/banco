package exception;

public class InvalidAccountIdentityException extends ValidationException{
    public InvalidAccountIdentityException(String message) {
        super(message);
    }
}
