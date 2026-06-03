package ui.formatter;

import model.valueobject.Money;

import java.text.NumberFormat;
import java.util.Locale;

public final class MoneyFormatter {

    private static final NumberFormat FORMAT =
            NumberFormat.getCurrencyInstance(
                    Locale.of("pt", "BR")
            );

    private MoneyFormatter() {
    }


    public static String format(Money money) {
        return FORMAT.format(
                money.value()
        );
    }
}
