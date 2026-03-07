package lab.productservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "product-command-service")
public record ProductCommand(
        @Id String id,
        int productNumber,
        String name,
        double price
) {
}
