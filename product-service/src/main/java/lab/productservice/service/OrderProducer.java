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

    public OrderProducer(KafkaTemplate<String, Order> kafkaTemplate,
                         @Value("${app.kafka.orders-topic}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void publishSampleOrders() {
        sampleOrders().forEach(order -> kafkaTemplate.send(topicName, order.orderNumber(), order));
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
