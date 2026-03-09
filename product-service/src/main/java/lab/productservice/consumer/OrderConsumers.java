package lab.productservice.consumer;

import lab.productservice.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumers {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumers.class);

    @KafkaListener(
            id = "order-default-consumer",
            topics = "${app.kafka.orders-topic}",
            groupId = "${app.kafka.consumer.default-group-id}")
    public void consumePublishedOrders(Order order) {
        LOGGER.info("default-consumer received order={}", order);
    }

    @KafkaListener(
            id = "order-from-beginning-consumer",
            topics = "${app.kafka.orders-topic}",
            groupId = "${app.kafka.consumer.from-beginning-group-id}")
    public void consumeFromBeginning(Order order,
                                     @Header(KafkaHeaders.OFFSET) long offset,
                                     @Header(KafkaHeaders.GROUP_ID) String groupId) {
        LOGGER.info("from-beginning consumer groupId={} offset={} order={}", groupId, offset, order);
    }

    @KafkaListener(
            id = "order-group-consumer-1",
            topics = "${app.kafka.orders-topic}",
            groupId = "${app.kafka.consumer.shared-group-id}",
            clientIdPrefix = "order-group-consumer-1")
    public void consumeWithGroupMemberOne(Order order,
                                          @Header(KafkaHeaders.OFFSET) long offset,
                                          @Header(KafkaHeaders.GROUP_ID) String groupId) {
        LOGGER.info("shared consumer 1 groupId={} offset={} order={}", groupId, offset, order);
    }

    @KafkaListener(
            id = "order-group-consumer-2",
            topics = "${app.kafka.orders-topic}",
            groupId = "${app.kafka.consumer.shared-group-id}",
            clientIdPrefix = "order-group-consumer-2")
    public void consumeWithGroupMemberTwo(Order order,
                                          @Header(KafkaHeaders.OFFSET) long offset,
                                          @Header(KafkaHeaders.GROUP_ID) String groupId) {
        LOGGER.info("shared consumer 2 groupId={} offset={} order={}", groupId, offset, order);
    }
}
