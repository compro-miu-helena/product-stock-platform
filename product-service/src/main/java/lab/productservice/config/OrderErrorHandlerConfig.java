package lab.productservice.config;

import lab.productservice.model.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class OrderErrorHandlerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderErrorHandlerConfig.class);

    @Bean
    public DefaultErrorHandler orderDefaultErrorHandler(
            KafkaOperations<String, Order> kafkaOperations,
            @Value("${app.kafka.orders-error-handler-dlt-topic}") String dltTopic) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaOperations,
                (ConsumerRecord<?, ?> record, Exception exception) -> {
                    LOGGER.error("Sending failed message to DLT topic={} originalTopic={} offset={}",
                            dltTopic, record.topic(), record.offset(), exception);
                    return new TopicPartition(dltTopic, record.partition());
                });
        return new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 2L));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Order> orderErrorHandlerKafkaListenerContainerFactory(
            ConsumerFactory<String, Order> consumerFactory,
            DefaultErrorHandler orderDefaultErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, Order> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(orderDefaultErrorHandler);
        return factory;
    }
}
