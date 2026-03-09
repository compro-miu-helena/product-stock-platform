package lab.productservice.service;

import lab.productservice.model.Order;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderBatchDemoService {

    private final KafkaTemplate<String, Order> defaultKafkaTemplate;
    private final KafkaTemplate<String, Order> orderProducerBatchKafkaTemplate;
    private final String producerBatchTopicName;
    private final String consumerBatchTopicName;
    private final AtomicInteger producerBatchSequence = new AtomicInteger(1);
    private final AtomicInteger consumerBatchSequence = new AtomicInteger(1);
    private final AtomicLong producerBatchEnabled = new AtomicLong(0);
    private final AtomicLong consumerBatchEnabled = new AtomicLong(0);

    public OrderBatchDemoService(@Qualifier("kafkaTemplate") KafkaTemplate<String, Order> defaultKafkaTemplate,
                                 @Qualifier("orderProducerBatchKafkaTemplate")
                                 KafkaTemplate<String, Order> orderProducerBatchKafkaTemplate,
                                 @Value("${app.kafka.orders-producer-batch-topic}") String producerBatchTopicName,
                                 @Value("${app.kafka.orders-consumer-batch-topic}") String consumerBatchTopicName) {
        this.defaultKafkaTemplate = defaultKafkaTemplate;
        this.orderProducerBatchKafkaTemplate = orderProducerBatchKafkaTemplate;
        this.producerBatchTopicName = producerBatchTopicName;
        this.consumerBatchTopicName = consumerBatchTopicName;
    }

    public void startProducerSideBatchDemo() {
        producerBatchEnabled.set(1);
    }

    public void stopProducerSideBatchDemo() {
        producerBatchEnabled.set(0);
    }

    public void startConsumerSideBatchDemo() {
        consumerBatchEnabled.set(1);
    }

    public void stopConsumerSideBatchDemo() {
        consumerBatchEnabled.set(0);
    }

    @Scheduled(fixedRate = 2000L)
    public void publishToProducerBatchTopic() {
        if (producerBatchEnabled.get() == 0) {
            return;
        }
        Order order = newTimedOrder("PRODUCER-BATCH", producerBatchSequence.getAndIncrement());
        orderProducerBatchKafkaTemplate.send(producerBatchTopicName, order.orderNumber(), order);
    }

    @Scheduled(fixedRate = 2000L)
    public void publishToConsumerBatchTopic() {
        if (consumerBatchEnabled.get() == 0) {
            return;
        }
        Order order = newTimedOrder("CONSUMER-BATCH", consumerBatchSequence.getAndIncrement());
        defaultKafkaTemplate.send(consumerBatchTopicName, order.orderNumber(), order);
    }

    private Order newTimedOrder(String prefix, int sequence) {
        return new Order(
                prefix + "-" + String.format("%04d", sequence),
                prefix + "-Customer-" + sequence,
                "USA",
                100.0 + sequence,
                Instant.now().toString());
    }
}
