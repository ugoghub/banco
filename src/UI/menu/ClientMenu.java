package UI.menu;

import model.Client;

public class ClientMenu {

    public static void show(Client client) {

        System.out.printf("""

                ===== MENU CLIENTE =====
                Cliente: %s

                1 - Criar conta bancária
                2 - Acessar conta
                3 - Excluir conta bancária
                4 - Excluir conta cliente
                0 - Logout
                """, client.getName().value());

        System.out.print("Escolha: ");
    }
}