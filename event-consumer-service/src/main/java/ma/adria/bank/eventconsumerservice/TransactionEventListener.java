package ma.adria.bank.eventconsumerservice;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionEventListener.class);

    @KafkaListener(topics = "bank.transactions.created", groupId = "event-consumer")
    public void onEvent(ConsumerRecord<String, String> record) {
        log.info("Événement reçu | clé(accountId)={} | partition={} | offset={} | valeur={}",
                record.key(), record.partition(), record.offset(), record.value());
    }
}