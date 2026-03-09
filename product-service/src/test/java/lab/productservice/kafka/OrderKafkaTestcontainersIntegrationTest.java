package lab.productservice.kafka;

import lab.productservice.client.StockClient;
import lab.productservice.consumer.OrderPartitionConsumers;
import lab.productservice.model.Order;
import lab.productservice.repository.ProductCommandRepository;
import lab.productservice.repository.ProductQueryRepository;
import lab.productservice.service.OrderPublishingService;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerConfig;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

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

@Testcontainers
@SpringBootTest(properties = {
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
        "app.kafka.orders-topic=tc-orders-topic",
        "app.kafka.orders-error-handler-topic=tc-orders-error-handler-topic",
        "app.kafka.orders-error-handler-dlt-topic=tc-orders-error-handler-topic-dlt",
        "app.kafka.orders-retryable-topic=tc-orders-retryable-topic",
        "app.kafka.orders-producer-batch-topic=tc-orders-producer-batch-topic",
        "app.kafka.orders-consumer-batch-topic=tc-orders-consumer-batch-topic"
})
class OrderKafkaTestcontainersIntegrationTest {

    @Container
    static final KafkaContainer KAFKA_CONTAINER =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.7.1"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
    }

    @Autowired
    private OrderPublishingService orderPublishingService;

    @Autowired
    @Qualifier("kafkaTemplate")
    private KafkaTemplate<Object, Object> kafkaTemplate;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @SpyBean
    private OrderPartitionConsumers orderPartitionConsumers;

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
    void shouldPublishOrdersWithTestcontainersKafka() {
        ensureTopicExists("tc-orders-topic", 3);

        try (Consumer<String, Order> consumer = createConsumer("tc-producer-test-group")) {
            consumer.subscribe(List.of("tc-orders-topic"));

            orderPublishingService.publishSampleOrdersWithUniqueKeys();

            List<ConsumerRecord<String, Order>> records = pollUntilCount(consumer, 6).stream()
                    .filter(record -> record.value().orderNumber().startsWith("ORD-"))
                    .toList();

            assertThat(records).hasSize(5);
            assertThat(records)
                    .extracting(record -> record.value().orderNumber())
                    .containsExactlyInAnyOrder("ORD-1001", "ORD-1002", "ORD-1003", "ORD-1004", "ORD-1005");
        }
    }

    @Test
    void shouldConsumeOrderWithTestcontainersKafka() throws Exception {
        ensureTopicExists("tc-orders-topic", 3);
        startContainer("order-partition-0-consumer", 1);
        Order order = new Order("TC-ORDER-0001", "Testcontainers Test", "USA", 55.0, Instant.now().toString());

        kafkaTemplate.send("tc-orders-topic", 0, order.orderNumber(), order).get();

        verify(orderPartitionConsumers, timeout(10000)).consumePartitionZero(
                argThat(received -> received.orderNumber().equals("TC-ORDER-0001")),
                anyLong(),
                eq(0));
    }

    private Consumer<String, Order> createConsumer(String groupId) {
        Map<String, Object> props = Map.of(
                org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                KAFKA_CONTAINER.getBootstrapServers(),
                org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, groupId,
                org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false",
                org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class,
                org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class,
                JsonDeserializer.TRUSTED_PACKAGES, "lab.productservice.model",
                JsonDeserializer.VALUE_DEFAULT_TYPE, Order.class
        );
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

    private void ensureTopicExists(String topic, int partitions) {
        try (AdminClient adminClient = AdminClient.create(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers()))) {
            adminClient.createTopics(List.of(new NewTopic(topic, partitions, (short) 1))).all().get();
        } catch (Exception ignored) {
            // Topic may already exist from the Spring admin client.
        }
    }
}
