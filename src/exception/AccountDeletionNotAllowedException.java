package exception;

public class AccountDeletionNotAllowedException extends GlobalException{
    public AccountDeletionNotAllowedException(String message) {
        super(message);
    }
}
