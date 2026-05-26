package exception;

public class InvalidOptionException extends ValidationException{
    public InvalidOptionException(String message) {
        super(message);
    }
}