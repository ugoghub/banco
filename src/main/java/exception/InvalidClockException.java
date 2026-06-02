package exception;

public class InvalidClockException extends DomainException{
    public InvalidClockException(String message) {
        super(message);
    }
}
