package lab.productservice.config;

import lab.productservice.service.OrderPublishingService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class OrderStartupPublisher implements ApplicationRunner {

    private final OrderPublishingService orderPublishingService;
    private final OrderKafkaProperties properties;

    public OrderStartupPublisher(OrderPublishingService orderPublishingService,
                                 OrderKafkaProperties properties) {
        this.orderPublishingService = orderPublishingService;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (properties.isPublishSampleOrdersOnStartup()) {
            orderPublishingService.publishSampleOrders();
        }
    }
}
