package lab.productservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class OrderKafkaCoreConfig {

    @Bean(name = "kafkaProducerFactory")
    public ProducerFactory<Object, Object> kafkaProducerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
    }

    @Bean(name = "kafkaTemplate")
    public KafkaTemplate<Object, Object> kafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory) {
        return new KafkaTemplate<>(kafkaProducerFactory);
    }

    @Bean(name = "defaultRetryTopicKafkaTemplate")
    public KafkaTemplate<Object, Object> defaultRetryTopicKafkaTemplate(
            ProducerFactory<Object, Object> kafkaProducerFactory) {
        return new KafkaTemplate<>(kafkaProducerFactory);
    }

    @Bean(name = "kafkaConsumerFactory")
    public ConsumerFactory<Object, Object> kafkaConsumerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }

    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);
        return factory;
    }

    @Bean(name = "transactionalKafkaProducerFactory")
    public ProducerFactory<Object, Object> transactionalKafkaProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "order-transactional-producer");
        DefaultKafkaProducerFactory<Object, Object> factory = new DefaultKafkaProducerFactory<>(props);
        factory.setTransactionIdPrefix("order-tx-");
        return factory;
    }

    @Bean(name = "transactionalKafkaTemplate")
    public KafkaTemplate<Object, Object> transactionalKafkaTemplate(
            ProducerFactory<Object, Object> transactionalKafkaProducerFactory) {
        return new KafkaTemplate<>(transactionalKafkaProducerFactory);
    }

    @Bean(name = "readCommittedKafkaConsumerFactory")
    public ConsumerFactory<Object, Object> readCommittedKafkaConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean(name = "readCommittedKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<Object, Object> readCommittedKafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> readCommittedKafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, readCommittedKafkaConsumerFactory);
        return factory;
    }
}
