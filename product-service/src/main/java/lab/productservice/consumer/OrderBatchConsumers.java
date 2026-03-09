package lab.productservice.consumer;

import lab.productservice.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class OrderBatchConsumers {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBatchConsumers.class);

    @KafkaListener(
            id = "order-producer-batch-consumer",
            topics = "${app.kafka.orders-producer-batch-topic}",
            groupId = "${app.kafka.consumer.producer-batch-group-id}")
    public void consumeProducerSideBatch(Order order) {
        LOGGER.info("producer-side-batch receivedAt={} order={}", Instant.now(), order);
    }

    @KafkaListener(
            id = "order-consumer-batch-consumer",
            topics = "${app.kafka.orders-consumer-batch-topic}",
            groupId = "${app.kafka.consumer.consumer-batch-group-id}",
            containerFactory = "orderConsumerBatchKafkaListenerContainerFactory")
    public void consumeConsumerSideBatch(List<Order> orders) {
        LOGGER.info("consumer-side-batch receivedAt={} batchSize={} orders={}", Instant.now(), orders.size(), orders);
    }
}
