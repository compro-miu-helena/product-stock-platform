package lab.productservice.kafka;

import lab.productservice.client.StockClient;
import lab.productservice.consumer.OrderConsumers;
import lab.productservice.model.Order;
import lab.productservice.repository.ProductCommandRepository;
import lab.productservice.repository.ProductQueryRepository;
import lab.productservice.service.OrderProducer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.listener.auto-startup=false",
        "spring.kafka.listener.missing-topics-fatal=false",
        "spring.cloud.config.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.consul.enabled=false",
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration",
        "app.kafka.publish-sample-orders-on-startup=false",
        "app.kafka.orders-topic=embedded-orders-topic",
        "app.kafka.orders-error-handler-topic=embedded-orders-error-handler-topic",
        "app.kafka.orders-error-handler-dlt-topic=embedded-orders-error-handler-topic-dlt",
        "app.kafka.orders-retryable-topic=embedded-orders-retryable-topic",
        "app.kafka.orders-producer-batch-topic=embedded-orders-producer-batch-topic",
        "app.kafka.orders-consumer-batch-topic=embedded-orders-consumer-batch-topic"
})
@EmbeddedKafka(partitions = 3, topics = {
        "embedded-orders-topic",
        "embedded-orders-error-handler-topic",
        "embedded-orders-error-handler-topic-dlt",
        "embedded-orders-retryable-topic",
        "embedded-orders-producer-batch-topic",
        "embedded-orders-consumer-batch-topic"
})
class OrderKafkaEmbeddedIntegrationTest {

    @Autowired
    private OrderProducer orderProducer;

    @Autowired
    @Qualifier("kafkaTemplate")
    private KafkaTemplate<Object, Object> kafkaTemplate;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @SpyBean
    private OrderConsumers orderConsumers;

    @MockBean
    private ProductCommandRepository productCommandRepository;

    @MockBean
    private ProductQueryRepository productQueryRepository;

    @MockBean
    private StockClient stockClient;

    @AfterEach
    void stopStartedContainers() {
        stopContainer("order-partition-0-consumer");
    }

    @Test
    void shouldPublishOrdersWithEmbeddedKafka() {
        try (Consumer<String, Order> consumer = createConsumer("embedded-producer-test-group")) {
            consumer.subscribe(List.of("embedded-orders-topic"));

            orderProducer.publishSampleOrdersWithUniqueKeys();

            List<ConsumerRecord<String, Order>> records = pollUntilCount(consumer, 5);

            assertThat(records).hasSize(5);
            assertThat(records)
                    .extracting(record -> record.value().orderNumber())
                    .containsExactlyInAnyOrder("ORD-1001", "ORD-1002", "ORD-1003", "ORD-1004", "ORD-1005");
        }
    }

    @Test
    void shouldConsumeOrderWithEmbeddedKafka() throws Exception {
        startContainer("order-partition-0-consumer", 1);
        Order order = new Order("EMB-ORDER-0001", "Embedded Test", "USA", 42.0, Instant.now().toString());

        kafkaTemplate.send("embedded-orders-topic", 0, order.orderNumber(), order).get();

        verify(orderConsumers, timeout(10000)).consumePartitionZero(
                argThat(received -> received.orderNumber().equals("EMB-ORDER-0001")),
                anyLong(),
                eq(0));
    }

    private Consumer<String, Order> createConsumer(String groupId) {
        Map<String, Object> props = KafkaTestUtils.consumerProps(groupId, "false", embeddedKafkaBroker);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "lab.productservice.model");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, Order.class);
        DefaultKafkaConsumerFactory<String, Order> consumerFactory = new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(Order.class, false));
        return consumerFactory.createConsumer();
    }

    private List<ConsumerRecord<String, Order>> pollUntilCount(Consumer<String, Order> consumer, int expectedCount) {
        List<ConsumerRecord<String, Order>> records = new ArrayList<>();
        long deadline = System.currentTimeMillis() + 10000;
        while (System.currentTimeMillis() < deadline && records.size() < expectedCount) {
            ConsumerRecords<String, Order> polled = consumer.poll(Duration.ofMillis(500));
            polled.forEach(records::add);
        }
        return records;
    }

    private void startContainer(String listenerId, int partitions) {
        MessageListenerContainer container = kafkaListenerEndpointRegistry.getListenerContainer(listenerId);
        assertThat(container).isNotNull();
        container.start();
        ContainerTestUtils.waitForAssignment(container, partitions);
    }

    private void stopContainer(String listenerId) {
        MessageListenerContainer container = kafkaListenerEndpointRegistry.getListenerContainer(listenerId);
        if (container != null && container.isRunning()) {
            container.stop();
        }
    }
}
