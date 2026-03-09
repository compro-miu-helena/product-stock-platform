package lab.productservice.service;

import lab.productservice.config.OrderKafkaProperties;
import lab.productservice.model.Order;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderBatchDemoService {

    private final KafkaTemplate<Object, Object> defaultKafkaTemplate;
    private final KafkaTemplate<String, Order> orderProducerBatchKafkaTemplate;
    private final OrderKafkaProperties properties;
    private final OrderFixtureFactory orderFixtureFactory;
    private final AtomicInteger producerBatchSequence = new AtomicInteger(1);
    private final AtomicInteger consumerBatchSequence = new AtomicInteger(1);
    private final AtomicLong producerBatchEnabled = new AtomicLong(0);
    private final AtomicLong consumerBatchEnabled = new AtomicLong(0);

    public OrderBatchDemoService(@Qualifier("kafkaTemplate") KafkaTemplate<Object, Object> defaultKafkaTemplate,
                                 @Qualifier("orderProducerBatchKafkaTemplate")
                                 KafkaTemplate<String, Order> orderProducerBatchKafkaTemplate,
                                 OrderKafkaProperties properties,
                                 OrderFixtureFactory orderFixtureFactory) {
        this.defaultKafkaTemplate = defaultKafkaTemplate;
        this.orderProducerBatchKafkaTemplate = orderProducerBatchKafkaTemplate;
        this.properties = properties;
        this.orderFixtureFactory = orderFixtureFactory;
    }

    public void startProducerSideBatchDemo() {
        producerBatchEnabled.set(1);
    }

    public void stopProducerSideBatchDemo() {
        producerBatchEnabled.set(0);
    }

    public void startConsumerSideBatchDemo() {
        consumerBatchEnabled.set(1);
    }

    public void stopConsumerSideBatchDemo() {
        consumerBatchEnabled.set(0);
    }

    @Scheduled(fixedRate = 2000L)
    public void publishToProducerBatchTopic() {
        if (producerBatchEnabled.get() == 0) {
            return;
        }
        Order order = orderFixtureFactory.timedOrder("PRODUCER-BATCH", producerBatchSequence.getAndIncrement());
        orderProducerBatchKafkaTemplate.send(properties.getOrdersProducerBatchTopic(), order.orderNumber(), order);
    }

    @Scheduled(fixedRate = 2000L)
    public void publishToConsumerBatchTopic() {
        if (consumerBatchEnabled.get() == 0) {
            return;
        }
        Order order = orderFixtureFactory.timedOrder("CONSUMER-BATCH", consumerBatchSequence.getAndIncrement());
        defaultKafkaTemplate.send(properties.getOrdersConsumerBatchTopic(), order.orderNumber(), order);
    }
}
