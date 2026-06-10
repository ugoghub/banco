package exception;

public class InvalidTransactionException extends ValidationException{
    public InvalidTransactionException(String message) {
        super(message);
    }
}
