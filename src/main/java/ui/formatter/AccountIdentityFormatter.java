package ui.formatter;

import model.valueobject.AccountIdentity;

public final class AccountIdentityFormatter {

    private AccountIdentityFormatter() {
    }

    public static String format(AccountIdentity accountIdentity) {
        return "Ag: " + accountIdentity.branch() + " | Conta: " + accountIdentity.accountNumber();
    }
}
