package exception;

public class TransferFailedException extends DomainException {
    public TransferFailedException(String message) {
        super(message);
    }
}
