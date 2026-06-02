package exception;

public class NoAccountsFoundException extends DomainException{
    public NoAccountsFoundException(String message) {
        super(message);
    }
}
