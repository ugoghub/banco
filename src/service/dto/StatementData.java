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
        UUID id)
{
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public String toString() {
        return """
                [%s]
                Data: %s
                Valor: R$ %s
                Origem: %s
                Destino: %s
                Id: %s
                """.formatted(
                type.getDescription(),
                dateTime.format(FORMATTER),
                amount,
                formatIdentity(source),
                formatIdentity(destination),
                id.toString()
        );
    }

    private String formatIdentity(AccountIdentity identity) {
        if (identity == null) return "-";

        return identity.branch() + " / " + identity.accountNumber();
    }
}
