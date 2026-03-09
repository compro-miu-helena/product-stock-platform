package lab.productservice.consumer;

import lab.productservice.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class OrderTransactionalConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderTransactionalConsumer.class);

    @KafkaListener(
            id = "order-transactional-consumer",
            topics = "${app.kafka.orders-transactional-topic}",
            groupId = "${app.kafka.consumer.transactional-group-id}",
            containerFactory = "readCommittedKafkaListenerContainerFactory")
    public void consumeCommittedOrder(Order order,
                                      @Header(KafkaHeaders.OFFSET) long offset,
                                      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        LOGGER.info("transactional-consumer topic={} offset={} order={}", topic, offset, order);
    }
}
