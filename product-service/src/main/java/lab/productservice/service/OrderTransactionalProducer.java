package lab.productservice.service;

import lab.productservice.model.Order;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderTransactionalProducer {

    private final KafkaTemplate<Object, Object> transactionalKafkaTemplate;
    private final String transactionalTopicName;

    public OrderTransactionalProducer(
            @Qualifier("transactionalKafkaTemplate") KafkaTemplate<Object, Object> transactionalKafkaTemplate,
            @Value("${app.kafka.orders-transactional-topic}") String transactionalTopicName) {
        this.transactionalKafkaTemplate = transactionalKafkaTemplate;
        this.transactionalTopicName = transactionalTopicName;
    }

    public void publishFiveOrdersInTransaction() {
        publishFiveOrdersInTransaction(false);
    }

    public void publishFiveOrdersInTransaction(boolean failAfterFourthMessage) {
        transactionalKafkaTemplate.executeInTransaction(operations -> {
            List<Order> orders = transactionalOrders();
            for (int i = 0; i < orders.size(); i++) {
                Order order = orders.get(i);
                operations.send(transactionalTopicName, order.orderNumber(), order);
                if (failAfterFourthMessage && i == 3) {
                    throw new IllegalStateException("Rolling back transactional order publish");
                }
            }
            return null;
        });
    }

    private List<Order> transactionalOrders() {
        return List.of(
                new Order("TX-ORD-1001", "Mia Clark", "USA", 300.00, "NEW"),
                new Order("TX-ORD-1002", "Noah Evans", "Canada", 450.00, "PROCESSING"),
                new Order("TX-ORD-1003", "Olivia Lopez", "Mexico", 520.00, "PAID"),
                new Order("TX-ORD-1004", "Paul Young", "UK", 610.00, "SHIPPED"),
                new Order("TX-ORD-1005", "Sara Kim", "Korea", 780.00, "DELIVERED")
        );
    }
}
