package exception;

public class InvalidPersonNameException extends ValidationException{
    public InvalidPersonNameException(String message) {
        super(message);
    }
}