package ui.menu;

import ui.messages.ConsoleMessages;
import service.dto.ClientData;

public final class ClientMenu {

    private ClientMenu() {
    }

    public static void show(ClientData client) {

        ConsoleMessages.info("""

                ===== MENU CLIENTE =====
                Bem vindo(a), %s

                1 - Criar conta bancária
                2 - Acessar conta bancária
                3 - Mostrar meus dados
                4 - Alterar meus dados
                5 - Excluir conta bancária
                6 - Excluir minha conta
                0 - Logout
                """,
                client.name().value().toUpperCase()
        );
    }
}