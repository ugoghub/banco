package UI.menu;

public final class InitialMenu {

    private InitialMenu() {
    }

    public static void show() {

        System.out.println("""

                ===== BANKLITE =====
                1 - Login
                2 - Criar conta
                0 - Sair
                """);

        System.out.print("Escolha: ");
    }
}
