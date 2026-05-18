package exception;

public class InvalidAccountTypeException extends GlobalException{
    public InvalidAccountTypeException(String message) {
        super(message);
    }
}