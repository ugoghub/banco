package ui.formatter;

import model.valueobject.AccountIdentity;
import model.valueobject.Money;
import service.dto.StatementData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class StatementFormatter {
    private StatementFormatter() {
    }

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static String format(StatementData statementData) {
        return """
                [%s]
                Data: %s
                Valor: %s
                Origem: %s
                Destino: %s
                Id: %s
                Operação: %s
                """.formatted(
                statementData.type(),
                formatDate(statementData.dateTime()),
                formatMoney(statementData.amount()),
                formatIdentity(statementData.source()),
                formatIdentity(statementData.destination()),
                formatId(statementData.id()),
                formatId(statementData.operationId())
        );
    }

    private static String formatIdentity(AccountIdentity accountIdentity) {
        if (accountIdentity == null) return "-";

        return AccountIdentityFormatter.format(accountIdentity);
    }

    private static String formatId(UUID id) {
        if (id == null) return "-";

        return id.toString();
    }

    private static String formatMoney(Money money) {
        return MoneyFormatter.format(money);
    }

    private static String formatDate(LocalDateTime date) {
        return date.format(FORMATTER);
    }
}
