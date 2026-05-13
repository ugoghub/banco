package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private final TypeTransaction type;
    private final BigDecimal amount;
    private final LocalDateTime dateTime;
    private final UUID sourceId;
    private final UUID destinationId;

    public Transaction(TypeTransaction type,
                       BigDecimal amount,
                       UUID sourceId,
                       UUID destinationId){

        this.type = type;
        this.amount = amount;
        this.dateTime = LocalDateTime.now();
        this.sourceId = sourceId;
        this.destinationId = destinationId;
    }

    public TypeTransaction getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public UUID getSourceId() {
        return sourceId;
    }

    public UUID getdestinationIdId() {
        return destinationId;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "dateTime=" + dateTime +
                ", type=" + type +
                ", sourceIdId=" + sourceId +
                ", destinationIdId=" + destinationId +
                ", amount=" + amount +
                '}';
    }
}