package exception;

public class InvalidAccountNumberException extends ValidationException{
    public InvalidAccountNumberException(String message) {
        super(message);
    }
}
