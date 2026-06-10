package exception;

public class InvalidTransferException extends BusinessRuleException {
    public InvalidTransferException(String message) {
        super(message);
    }
}
