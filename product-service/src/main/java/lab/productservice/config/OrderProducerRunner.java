package lab.productservice.config;

import lab.productservice.service.OrderProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class OrderProducerRunner implements ApplicationRunner {

    private final OrderProducer orderProducer;
    private final boolean publishOnStartup;

    public OrderProducerRunner(OrderProducer orderProducer,
                               @Value("${app.kafka.publish-sample-orders-on-startup:true}") boolean publishOnStartup) {
        this.orderProducer = orderProducer;
        this.publishOnStartup = publishOnStartup;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (publishOnStartup) {
            orderProducer.publishSampleOrders();
        }
    }
}
