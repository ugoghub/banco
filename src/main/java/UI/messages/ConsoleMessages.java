package UI.messages;

import exception.DomainException;

public final class ConsoleMessages {
    private ConsoleMessages() {
    }

    public static void info(String message){
        System.out.println(message);
    }

    public static <T> void info(String msg, T value){
        System.out.printf(msg, value);
    }

    public static void success(String message){
        System.out.println(message);
    }

    public static <T> void success(String msg, T value){
        System.out.printf(msg, value);
    }

    public static void error(DomainException e){
        System.err.println("Erro: " + e.getMessage());
    }
}
