package lab.productservice.service;

import lab.productservice.config.OrderKafkaProperties;
import lab.productservice.model.Order;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderPublishingService {

    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final OrderKafkaProperties properties;
    private final OrderFixtureFactory orderFixtureFactory;

    public OrderPublishingService(@Qualifier("kafkaTemplate") KafkaTemplate<Object, Object> kafkaTemplate,
                                  OrderKafkaProperties properties,
                                  OrderFixtureFactory orderFixtureFactory) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
        this.orderFixtureFactory = orderFixtureFactory;
    }

    public void publishSampleOrders() {
        publishSampleOrdersWithSameKey();
    }

    public void publishSampleOrdersWithSameKey() {
        String sharedKey = "all-orders";
        orderFixtureFactory.sampleOrders()
                .forEach(order -> kafkaTemplate.send(properties.getOrdersTopic(), sharedKey, order));
    }

    public void publishSampleOrdersWithUniqueKeys() {
        orderFixtureFactory.sampleOrders()
                .forEach(order -> kafkaTemplate.send(properties.getOrdersTopic(), order.orderNumber(), order));
    }

    public void publishDefaultErrorHandlerOrder(Order order) {
        kafkaTemplate.send(properties.getOrdersErrorHandlerTopic(), order.orderNumber(), order);
    }

    public void publishRetryableOrder(Order order) {
        kafkaTemplate.send(properties.getOrdersRetryableTopic(), order.orderNumber(), order);
    }
}
