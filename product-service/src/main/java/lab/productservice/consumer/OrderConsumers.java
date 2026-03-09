package lab.productservice.consumer;

import lab.productservice.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumers {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumers.class);

    @KafkaListener(
            id = "order-partition-0-consumer",
            groupId = "${app.kafka.consumer.partition-group-id}",
            topicPartitions = @TopicPartition(topic = "${app.kafka.orders-topic}", partitions = "0"))
    public void consumePartitionZero(Order order,
                                     @Header(KafkaHeaders.OFFSET) long offset,
                                     @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        LOGGER.info("partition-listener-0 offset={} partition={} order={}", offset, partition, order);
    }

    @KafkaListener(
            id = "order-partition-1-consumer",
            groupId = "${app.kafka.consumer.partition-group-id}",
            topicPartitions = @TopicPartition(topic = "${app.kafka.orders-topic}", partitions = "1"))
    public void consumePartitionOne(Order order,
                                    @Header(KafkaHeaders.OFFSET) long offset,
                                    @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        LOGGER.info("partition-listener-1 offset={} partition={} order={}", offset, partition, order);
    }

    @KafkaListener(
            id = "order-partition-2-consumer",
            groupId = "${app.kafka.consumer.partition-group-id}",
            topicPartitions = @TopicPartition(topic = "${app.kafka.orders-topic}", partitions = "2"))
    public void consumePartitionTwo(Order order,
                                    @Header(KafkaHeaders.OFFSET) long offset,
                                    @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        LOGGER.info("partition-listener-2 offset={} partition={} order={}", offset, partition, order);
    }
}
