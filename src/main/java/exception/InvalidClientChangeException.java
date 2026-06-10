package exception;

public class InvalidClientChangeException extends BusinessRuleException{
    public InvalidClientChangeException(String message) {
        super(message);
    }
}
