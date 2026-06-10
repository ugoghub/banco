package exception;

public abstract class BusinessRuleException extends DomainException{
    public BusinessRuleException(String message) {
        super(message);
    }
}
