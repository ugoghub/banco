package exception;

public class NoAccountsFoundException extends BusinessRuleException{
    public NoAccountsFoundException(String message) {
        super(message);
    }
}
