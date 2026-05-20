package UI.error;

import exception.DomainException;

public class ErrorHandler {
    public static void showError(DomainException e){
        System.out.println("Erro: " + e.getMessage());
    }
}
