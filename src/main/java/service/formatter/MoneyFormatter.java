package service.formatter;

import model.valueobject.Money;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class MoneyFormatter {

    private MoneyFormatter() {
    }

    public static String format(Money money) {
        return NumberFormat
                .getCurrencyInstance(
                        Locale.of("pt", "BR")
                ).format(
                        money.value()
                );
    }
}
