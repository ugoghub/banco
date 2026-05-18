package exception;

public class EmailAlreadyExistsException extends GlobalException{
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}