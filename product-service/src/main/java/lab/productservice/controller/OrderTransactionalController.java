package lab.productservice.controller;

import lab.productservice.service.OrderTransactionalProducer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders/transactions")
public class OrderTransactionalController {

    private final OrderTransactionalProducer orderTransactionalProducer;

    public OrderTransactionalController(OrderTransactionalProducer orderTransactionalProducer) {
        this.orderTransactionalProducer = orderTransactionalProducer;
    }

    @PostMapping("/commit")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publishCommittedTransaction() {
        orderTransactionalProducer.publishFiveOrdersInTransaction();
    }

    @PostMapping("/rollback")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publishRolledBackTransaction() {
        orderTransactionalProducer.publishFiveOrdersInTransaction(true);
    }
}
