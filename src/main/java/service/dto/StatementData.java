package service.dto;

import model.TransactionType;
import model.valueobject.AccountIdentity;
import model.valueobject.Money;

import java.time.LocalDateTime;
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
{}
