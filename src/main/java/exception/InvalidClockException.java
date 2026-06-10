package exception;

public class InvalidClockException extends ValidationException{
    public InvalidClockException(String message) {
        super(message);
    }
}
