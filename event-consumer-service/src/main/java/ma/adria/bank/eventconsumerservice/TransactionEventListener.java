package ma.adria.bank.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransactionEventListener {

    @KafkaListener(topics = "bank.transactions.created", groupId = "event-consumer")
    public void onEvent(ConsumerRecord<String, String> record) {
        log.info("Événement reçu | clé(accountId)={} | partition={} | offset={} | valeur={}",
                record.key(), record.partition(), record.offset(), record.value());
    }
}