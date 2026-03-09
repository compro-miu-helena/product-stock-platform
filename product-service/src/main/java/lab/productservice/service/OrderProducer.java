package lab.productservice.service;

import lab.productservice.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderProducer {

    private final KafkaTemplate<String, Order> kafkaTemplate;
    private final String topicName;
    private final String errorHandlerTopicName;
    private final String retryableTopicName;

    public OrderProducer(KafkaTemplate<String, Order> kafkaTemplate,
                         @Value("${app.kafka.orders-topic}") String topicName,
                         @Value("${app.kafka.orders-error-handler-topic}") String errorHandlerTopicName,
                         @Value("${app.kafka.orders-retryable-topic}") String retryableTopicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
        this.errorHandlerTopicName = errorHandlerTopicName;
        this.retryableTopicName = retryableTopicName;
    }

    public void publishSampleOrders() {
        publishSampleOrdersWithSameKey();
    }

    public void publishSampleOrdersWithSameKey() {
        String sharedKey = "all-orders";
        sampleOrders().forEach(order -> kafkaTemplate.send(topicName, sharedKey, order));
    }

    public void publishSampleOrdersWithUniqueKeys() {
        sampleOrders().forEach(order -> kafkaTemplate.send(topicName, order.orderNumber(), order));
    }

    public void publishDefaultErrorHandlerOrder(Order order) {
        kafkaTemplate.send(errorHandlerTopicName, order.orderNumber(), order);
    }

    public void publishRetryableOrder(Order order) {
        kafkaTemplate.send(retryableTopicName, order.orderNumber(), order);
    }

    private List<Order> sampleOrders() {
        return List.of(
                new Order("ORD-1001", "Alice Johnson", "USA", 1250.50, "NEW"),
                new Order("ORD-1002", "Bruno Silva", "Brazil", 875.00, "PROCESSING"),
                new Order("ORD-1003", "Claire Martin", "France", 430.25, "PAID"),
                new Order("ORD-1004", "David Brown", "Canada", 2210.10, "SHIPPED"),
                new Order("ORD-1005", "Elena Rossi", "Italy", 640.75, "DELIVERED")
        );
    }
}
