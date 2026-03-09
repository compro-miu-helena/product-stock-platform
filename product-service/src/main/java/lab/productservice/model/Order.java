package lab.productservice.model;

public record Order(
        String orderNumber,
        String customerName,
        String customerCountry,
        double amount,
        String status) {
}
