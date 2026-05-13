package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Transaction {
    private final TransactionType type;
    private final BigDecimal amount;
    private final LocalDateTime dateTime;
    private final UUID sourceId;
    private final UUID destinationId;

    public Transaction(TransactionType type,
                       BigDecimal amount,
                       UUID sourceId,
                       UUID destinationId){

        this.type = type;
        this.amount = amount;
        this.dateTime = LocalDateTime.now();
        this.sourceId = sourceId;
        this.destinationId = destinationId;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDateTime() {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public UUID getSourceId() {
        return sourceId;
    }

    public UUID getDestinationId() {
        return destinationId;
    }

    @Override
    public String toString() {
        return "[" + type + "],\n" +
                "dateTime = " + getDateTime() +
                ",\n amount = " + amount +
                ",\n sourceId = " + sourceId +
                ",\n destinationId = " + destinationId +
                '}';
    }
}