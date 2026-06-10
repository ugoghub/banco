package exception;

public class AccountDeletionNotAllowedException extends BusinessRuleException {
    public AccountDeletionNotAllowedException(String message) {
        super(message);
    }
}
