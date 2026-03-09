package lab.productservice.consumer;

import lab.productservice.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class OrderErrorHandlerConsumers {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderErrorHandlerConsumers.class);

    @KafkaListener(
            id = "order-default-error-handler-consumer",
            topics = "${app.kafka.orders-error-handler-topic}",
            groupId = "${app.kafka.consumer.error-handler-group-id}",
            containerFactory = "orderErrorHandlerKafkaListenerContainerFactory")
    public void consumeWithDefaultErrorHandler(Order order,
                                               @Header(KafkaHeaders.OFFSET) long offset,
                                               @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        LOGGER.info("default-error-handler consumer topic={} offset={} order={}", topic, offset, order);
        if (shouldFail(order)) {
            throw new IllegalStateException("DefaultErrorHandler demo failure for order " + order.orderNumber());
        }
    }

    @KafkaListener(
            id = "order-default-error-handler-dlt-consumer",
            topics = "${app.kafka.orders-error-handler-dlt-topic}",
            groupId = "${app.kafka.consumer.error-handler-dlt-group-id}")
    public void consumeDefaultErrorHandlerDlt(Order order,
                                              @Header(KafkaHeaders.OFFSET) long offset,
                                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        LOGGER.info("default-error-handler DLT topic={} offset={} order={}", topic, offset, order);
    }

    private boolean shouldFail(Order order) {
        return "FAIL".equalsIgnoreCase(order.status());
    }
}
