package exception;

public class InvalidClientIdException extends ValidationException{
    public InvalidClientIdException(String message) {
        super(message);
    }
}
