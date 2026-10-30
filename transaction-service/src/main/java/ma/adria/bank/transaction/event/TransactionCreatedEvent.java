package ma.adria.bank.transaction.event;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionCreatedEvent(UUID eventId, String eventType, UUID transactionId,
                                      UUID accountId, BigDecimal amount, String currency,
                                      String occurredAt) {}