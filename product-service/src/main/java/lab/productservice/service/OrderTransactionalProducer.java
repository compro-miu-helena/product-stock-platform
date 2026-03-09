package lab.productservice.service;

import lab.productservice.config.OrderKafkaProperties;
import lab.productservice.model.Order;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderTransactionalProducer {

    private final KafkaTemplate<Object, Object> transactionalKafkaTemplate;
    private final OrderKafkaProperties properties;
    private final OrderFixtureFactory orderFixtureFactory;

    public OrderTransactionalProducer(
            @Qualifier("transactionalKafkaTemplate") KafkaTemplate<Object, Object> transactionalKafkaTemplate,
            OrderKafkaProperties properties,
            OrderFixtureFactory orderFixtureFactory) {
        this.transactionalKafkaTemplate = transactionalKafkaTemplate;
        this.properties = properties;
        this.orderFixtureFactory = orderFixtureFactory;
    }

    public void publishFiveOrdersInTransaction() {
        publishFiveOrdersInTransaction(false);
    }

    public void publishFiveOrdersInTransaction(boolean failAfterFourthMessage) {
        transactionalKafkaTemplate.executeInTransaction(operations -> {
            java.util.List<Order> orders = orderFixtureFactory.transactionalOrders();
            for (int i = 0; i < orders.size(); i++) {
                Order order = orders.get(i);
                operations.send(properties.getOrdersTransactionalTopic(), order.orderNumber(), order);
                if (failAfterFourthMessage && i == 3) {
                    throw new IllegalStateException("Rolling back transactional order publish");
                }
            }
            return null;
        });
    }
}
