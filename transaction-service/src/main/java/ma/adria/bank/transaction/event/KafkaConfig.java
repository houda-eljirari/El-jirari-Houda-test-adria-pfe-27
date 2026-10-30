package ma.adria.bank.transaction.event;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    NewTopic transactionsCreatedTopic() {
        return TopicBuilder.name(TransactionEventPublisher.TOPIC).partitions(3).replicas(1).build();
    }
}