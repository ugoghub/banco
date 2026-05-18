package model;

import model.valueObjects.AccountIdentity;
import model.valueObjects.Money;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Transaction {
    private final UUID id;
    private final TransactionType type;
    private final Money amount;
    private final AccountIdentity sourceIdentity;
    private final AccountIdentity destinationIdentity;
    private final LocalDateTime dateTime;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Transaction(TransactionType type,
                       Money amount,
                       AccountIdentity sourceId,
                       AccountIdentity destinationId) {

        this.id = UUID.randomUUID();
        this.type = type;
        this.amount = amount;
        this.dateTime = LocalDateTime.now();
        this.sourceIdentity = sourceId;
        this.destinationIdentity = destinationId;
    }

    public UUID getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public Money getAmount() {
        return amount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public AccountIdentity getSourceId() {
        return sourceIdentity;
    }

    public AccountIdentity getDestinationId() {
        return destinationIdentity;
    }

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
                formatIdentity(sourceIdentity),
                formatIdentity(destinationIdentity),
                id.toString()
        );
    }

    private String formatIdentity(AccountIdentity identity) {
        if (identity == null) return "-";

        return identity.branch() + " / " + identity.accountNumber();
    }
}