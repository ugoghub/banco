package exception;

public class AccountOwnershipException extends DomainException {
    public AccountOwnershipException(String message) {
        super(message);
    }
}