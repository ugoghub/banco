package exception;

public class AccountDeletionNotAllowedException extends DomainException {
    public AccountDeletionNotAllowedException(String message) {
        super(message);
    }
}
