package ui.menu;

import ui.messages.ConsoleMessages;

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
