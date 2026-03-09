package lab.productservice.consumer;

import lab.productservice.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class OrderRetryableConsumers {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRetryableConsumers.class);

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 0L),
            dltTopicSuffix = "-dlt",
            dltStrategy = DltStrategy.FAIL_ON_ERROR)
    @KafkaListener(
            id = "order-retryable-consumer",
            topics = "${app.kafka.orders-retryable-topic}",
            groupId = "${app.kafka.consumer.retryable-group-id}")
    public void consumeWithRetryableTopic(Order order) {
        LOGGER.info("retryable-topic consumer order={}", order);
        if (shouldFail(order)) {
            throw new IllegalStateException("RetryableTopic demo failure for order " + order.orderNumber());
        }
    }

    @DltHandler
    public void consumeRetryableDlt(Order order) {
        LOGGER.info("retryable-topic DLT order={}", order);
    }

    private boolean shouldFail(Order order) {
        return "FAIL".equalsIgnoreCase(order.status());
    }
}
