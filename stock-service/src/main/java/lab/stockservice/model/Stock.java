package lab.stockservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stock-command-service")
public record Stock(
        @Id String id,
        int productNumber,
        int quantity
) {
}
