package lab.productservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "product-command-service")
public record ProductEvent(
        @Id String id,
        int productNumber,
        long sequenceNumber,
        ProductEventType eventType,
        String name,
        double price
) {
}
