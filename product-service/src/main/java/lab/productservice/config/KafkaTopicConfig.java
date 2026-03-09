package lab.productservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic ordersTopic(OrderKafkaProperties properties) {
        return TopicBuilder.name(properties.getOrdersTopic())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ordersErrorHandlerTopic(OrderKafkaProperties properties) {
        return TopicBuilder.name(properties.getOrdersErrorHandlerTopic())
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ordersErrorHandlerDltTopic(OrderKafkaProperties properties) {
        return TopicBuilder.name(properties.getOrdersErrorHandlerDltTopic())
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ordersRetryableTopic(OrderKafkaProperties properties) {
        return TopicBuilder.name(properties.getOrdersRetryableTopic())
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ordersProducerBatchTopic(OrderKafkaProperties properties) {
        return TopicBuilder.name(properties.getOrdersProducerBatchTopic())
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ordersConsumerBatchTopic(OrderKafkaProperties properties) {
        return TopicBuilder.name(properties.getOrdersConsumerBatchTopic())
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ordersTransactionalTopic(OrderKafkaProperties properties) {
        return TopicBuilder.name(properties.getOrdersTransactionalTopic())
                .partitions(1)
                .replicas(1)
                .build();
    }
}
