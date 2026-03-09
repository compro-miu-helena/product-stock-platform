package lab.productservice.controller;

import lab.productservice.service.OrderBatchDemoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders/batch-demo")
public class OrderBatchDemoController {

    private final OrderBatchDemoService orderBatchDemoService;

    public OrderBatchDemoController(OrderBatchDemoService orderBatchDemoService) {
        this.orderBatchDemoService = orderBatchDemoService;
    }

    @PostMapping("/producer-side/start")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void startProducerSideBatchDemo() {
        orderBatchDemoService.startProducerSideBatchDemo();
    }

    @PostMapping("/producer-side/stop")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void stopProducerSideBatchDemo() {
        orderBatchDemoService.stopProducerSideBatchDemo();
    }

    @PostMapping("/consumer-side/start")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void startConsumerSideBatchDemo() {
        orderBatchDemoService.startConsumerSideBatchDemo();
    }

    @PostMapping("/consumer-side/stop")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void stopConsumerSideBatchDemo() {
        orderBatchDemoService.stopConsumerSideBatchDemo();
    }
}
