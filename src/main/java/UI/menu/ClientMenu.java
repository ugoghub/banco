package UI.menu;

import service.dto.ClientData;

public class ClientMenu {

    public static void show(ClientData client) {

        System.out.printf("""

                ===== MENU CLIENTE =====
                Cliente: %s

                1 - Criar conta bancária
                2 - Acessar conta
                3 - Mostrar dados da conta
                4 - Alterar dados da conta
                5 - Excluir conta bancária
                6 - Excluir conta cliente
                0 - Logout
                """,
                client.name().toUpperCase()
        );

        System.out.print("Escolha: ");
    }
}