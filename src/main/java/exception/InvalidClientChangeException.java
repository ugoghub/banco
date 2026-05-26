package exception;

public class InvalidClientChangeException extends ValidationException{
    public InvalidClientChangeException(String message) {
        super(message);
    }
}
