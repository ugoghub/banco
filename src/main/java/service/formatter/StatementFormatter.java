package service.formatter;

import model.valueobject.AccountIdentity;
import service.dto.StatementData;

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
                statementData.dateTime().format(FORMATTER),
                statementData.amount().value(),
                formatIdentity(statementData.source()),
                formatIdentity(statementData.destination()),
                statementData.id().toString(),
                formatOperationId(statementData.operationId())
        );
    }

    private static String formatIdentity(AccountIdentity identity) {
        if (identity == null) return "-";

        return identity.branch() + " / " + identity.accountNumber();
    }

    private static  String formatOperationId(UUID operationId) {
        if (operationId == null) return "-";

        return operationId.toString();
    }
}
