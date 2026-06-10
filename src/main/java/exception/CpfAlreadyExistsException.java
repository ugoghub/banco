package exception;

public class CpfAlreadyExistsException extends BusinessRuleException {
    public CpfAlreadyExistsException(String message) {
        super(message);
    }
}
