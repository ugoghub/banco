package exception;

public class InvalidCpfException extends ValidationException{
    public InvalidCpfException(String message) {
        super(message);
    }
}