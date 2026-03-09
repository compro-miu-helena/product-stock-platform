package lab.productservice.service;

import lab.productservice.model.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class OrderFixtureFactory {

    public List<Order> sampleOrders() {
        return List.of(
                new Order("ORD-1001", "Alice Johnson", "USA", 1250.50, "NEW"),
                new Order("ORD-1002", "Bruno Silva", "Brazil", 875.00, "PROCESSING"),
                new Order("ORD-1003", "Claire Martin", "France", 430.25, "PAID"),
                new Order("ORD-1004", "David Brown", "Canada", 2210.10, "SHIPPED"),
                new Order("ORD-1005", "Elena Rossi", "Italy", 640.75, "DELIVERED")
        );
    }

    public List<Order> transactionalOrders() {
        return List.of(
                new Order("TX-ORD-1001", "Mia Clark", "USA", 300.00, "NEW"),
                new Order("TX-ORD-1002", "Noah Evans", "Canada", 450.00, "PROCESSING"),
                new Order("TX-ORD-1003", "Olivia Lopez", "Mexico", 520.00, "PAID"),
                new Order("TX-ORD-1004", "Paul Young", "UK", 610.00, "SHIPPED"),
                new Order("TX-ORD-1005", "Sara Kim", "Korea", 780.00, "DELIVERED")
        );
    }

    public Order defaultErrorHandlerFailureOrder() {
        return new Order("ORD-ERR-1001", "Error Handler Demo", "USA", 100.00, "FAIL");
    }

    public Order retryableFailureOrder() {
        return new Order("ORD-RETRY-1001", "Retryable Demo", "USA", 100.00, "FAIL");
    }

    public Order timedOrder(String prefix, int sequence) {
        return new Order(
                prefix + "-" + String.format("%04d", sequence),
                prefix + "-Customer-" + sequence,
                "USA",
                100.0 + sequence,
                Instant.now().toString());
    }
}
