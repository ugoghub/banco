package UI.error;

import exception.DomainException;

public final class ErrorHandler {

    private ErrorHandler() {}

    public static void printError(DomainException e){
        System.out.println("Erro: " + e.getMessage());
    }
}
