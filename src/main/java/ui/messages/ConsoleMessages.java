package ui.messages;

import exception.DomainException;

public final class ConsoleMessages {

    private ConsoleMessages() {}

    private static final String RESET = "\u001B[0m";

    private static final String SUCCESS_COLOR = "\u001B[32m";
    private static final String ERROR_COLOR = "\u001B[31m";
    private static final String HIGHLIGHT_COLOR = "\u001B[34m";

    public static void info(String msg){
        System.out.printf(msg);
    }

    public static void infoLn(String msg){
        System.out.println(msg);
    }

    public static void info(String msg, Object... values) {
        System.out.printf(msg, values);
    }

    public static void success(String msg) {
        System.out.println(SUCCESS_COLOR + msg + RESET);
    }

    public static void success(String msg, Object... values) {
        System.out.printf(SUCCESS_COLOR + msg + "%n" + RESET, values);
    }

    public static void highlight(String msg) {
        System.out.println(HIGHLIGHT_COLOR + msg + RESET);
    }

    public static void highlight(String msg, Object ... values) {
        System.out.printf(HIGHLIGHT_COLOR + msg + "%n" + RESET, values);
    }

    public static void error(DomainException e) {
        System.out.println(ERROR_COLOR + e.getMessage() + RESET);
    }

    public static void error(String message) {
        System.out.println(ERROR_COLOR + message + RESET);
    }
}
