package lab.productservice.controller;

import lab.productservice.service.OrderFixtureFactory;
import lab.productservice.service.OrderPublishingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderPublishingController {

    private final OrderPublishingService orderPublishingService;
    private final OrderFixtureFactory orderFixtureFactory;

    public OrderPublishingController(OrderPublishingService orderPublishingService,
                                     OrderFixtureFactory orderFixtureFactory) {
        this.orderPublishingService = orderPublishingService;
        this.orderFixtureFactory = orderFixtureFactory;
    }

    @PostMapping("/publish-sample")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publishSampleOrders() {
        orderPublishingService.publishSampleOrders();
    }

    @PostMapping("/publish-same-key")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publishSampleOrdersWithSameKey() {
        orderPublishingService.publishSampleOrdersWithSameKey();
    }

    @PostMapping("/publish-unique-keys")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publishSampleOrdersWithUniqueKeys() {
        orderPublishingService.publishSampleOrdersWithUniqueKeys();
    }

    @PostMapping("/publish-default-error-handler-failure")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publishDefaultErrorHandlerFailure() {
        orderPublishingService.publishDefaultErrorHandlerOrder(orderFixtureFactory.defaultErrorHandlerFailureOrder());
    }

    @PostMapping("/publish-retryable-failure")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publishRetryableFailure() {
        orderPublishingService.publishRetryableOrder(orderFixtureFactory.retryableFailureOrder());
    }
}
