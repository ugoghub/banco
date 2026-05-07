package exception;

public class AccountNotFoundException extends GlobalException{
    public AccountNotFoundException(String message) {
        super(message);
    }
}
