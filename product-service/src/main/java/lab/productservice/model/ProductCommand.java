package lab.productservice.model;

public record ProductCommand(
        int productNumber,
        String name,
        double price
) {
}
