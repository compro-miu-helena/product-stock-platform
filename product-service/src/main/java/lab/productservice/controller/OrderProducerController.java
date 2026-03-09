package lab.productservice.controller;

import lab.productservice.model.Order;
import lab.productservice.service.OrderProducer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderProducerController {

    private final OrderProducer orderProducer;

    public OrderProducerController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    @PostMapping("/publish-sample")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publishSampleOrders() {
        orderProducer.publishSampleOrders();
    }

    @PostMapping("/publish-same-key")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publishSampleOrdersWithSameKey() {
        orderProducer.publishSampleOrdersWithSameKey();
    }

    @PostMapping("/publish-unique-keys")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publishSampleOrdersWithUniqueKeys() {
        orderProducer.publishSampleOrdersWithUniqueKeys();
    }

    @PostMapping("/publish-default-error-handler-failure")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publishDefaultErrorHandlerFailure() {
        orderProducer.publishDefaultErrorHandlerOrder(
                new Order("ORD-ERR-1001", "Error Handler Demo", "USA", 100.00, "FAIL"));
    }

    @PostMapping("/publish-retryable-failure")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publishRetryableFailure() {
        orderProducer.publishRetryableOrder(
                new Order("ORD-RETRY-1001", "Retryable Demo", "USA", 100.00, "FAIL"));
    }
}
