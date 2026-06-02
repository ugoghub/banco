package ui.menu;

import ui.messages.ConsoleMessages;
import model.valueobject.AccountIdentity;

public final class AccountMenu {

    private AccountMenu() {
    }

    public static void show(
            AccountIdentity accountIdentity
    ) {

        ConsoleMessages.info("""
                
                ===== %s =====
                1 - Depositar
                2 - Sacar
                3 - Ver saldo
                4 - Transferir
                5 - Extrato
                0 - Voltar
                """, accountIdentity);
    }
}