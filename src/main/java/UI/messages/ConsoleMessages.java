package UI.messages;

import exception.DomainException;

public final class ConsoleMessages {

    private ConsoleMessages() {}

    private static final String RESET = "\u001B[0m";

    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    public static final String BLUE   = "\u001B[34m";

    public static void info(String msg){
        System.out.printf(msg);
    }

    public static void infoLn(String msg){
        System.out.println(msg);
    }

    public static void info(String msg, Object... values) {
        System.out.printf(msg, values);
    }

    public static void successLn(String msg) {
        System.out.println(GREEN + msg + RESET);
    }

    public static void successLn(String msg, Object... values) {
        System.out.printf(GREEN + msg + "%n" + RESET, values);
    }

    public static void highlightLn(String msg) {
        System.out.println(BLUE + msg + RESET);
    }

    public static void highlightLn(String msg, Object ... values) {
        System.out.printf(BLUE + msg + "%n" + RESET, values);
    }

    public static void error(DomainException e) {
        System.out.println(RED + e.getMessage() + RESET);
    }

    public static void error(String message) {
        System.out.println(RED + message + RESET);
    }
}
