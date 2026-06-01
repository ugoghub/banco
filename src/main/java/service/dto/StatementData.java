package service.dto;

import model.TransactionType;
import model.valueObjects.AccountIdentity;
import model.valueObjects.Money;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record StatementData(
        TransactionType type,
        LocalDateTime dateTime,
        AccountIdentity source,
        AccountIdentity destination,
        Money amount,
        UUID id,
        UUID operationId
        )
{
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public String toString() {
        return """
                [%s]
                Data: %s
                Valor: %s
                Origem: %s
                Destino: %s
                Id: %s
                Operação: %s
                """.formatted(
                type,
                dateTime.format(FORMATTER),
                amount,
                formatIdentity(source),
                formatIdentity(destination),
                id.toString(),
                formatOperationId(operationId)
        );
    }

    private String formatIdentity(AccountIdentity identity) {
        if (identity == null) return "-";

        return identity.branch() + " / " + identity.accountNumber();
    }
    private String formatOperationId(UUID operationId) {
        if (operationId == null) return "-";

        return operationId.toString();
    }

}
