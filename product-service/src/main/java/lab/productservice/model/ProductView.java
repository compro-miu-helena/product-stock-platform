package lab.productservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "product-query-service")
public record ProductView(
        @Id String id,
        int productNumber,
        String name,
        double price,
        int numberInStock
) {
}
