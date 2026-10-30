package ma.adria.bank.transaction.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.adria.bank.transaction.domain.Transaction;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventPublisher {
    public static final String TOPIC = "bank.transactions.created";
    private final KafkaTemplate<String, Object> kafka;

    public void publish(Transaction t) {
        TransactionCreatedEvent event = new TransactionCreatedEvent(
                UUID.randomUUID(), "TransactionCreated", t.getId(), t.getAccountId(),
                t.getAmount(), t.getCurrency(), t.getCreatedAt().toString());
        kafka.send(TOPIC, t.getAccountId().toString(), event);   // clé = accountId
        log.info("Événement publié: {}", event);
    }
}