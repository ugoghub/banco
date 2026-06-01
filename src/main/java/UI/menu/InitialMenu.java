package UI.menu;

import UI.messages.ConsoleMessages;

public final class InitialMenu {

    private InitialMenu() {
    }

    public static void show() {

        ConsoleMessages.info("""

                ===== BANKLITE =====
                1 - Login
                2 - Criar conta
                0 - Sair
                """);
    }
}
