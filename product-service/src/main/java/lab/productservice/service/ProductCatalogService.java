package lab.productservice.service;

import lab.productservice.model.Product;
import org.springframework.stereotype.Service;

@Service
public class ProductCatalogService {

    private static final int FALLBACK_STOCK = -1;

    private final StockLookupService stockLookupService;

    public ProductCatalogService(StockLookupService stockLookupService) {
        this.stockLookupService = stockLookupService;
    }

    public Product getProduct(int productNumber) {
        int stock = stockLookupService.fetchStock(productNumber);
        return new Product(productNumber, resolveName(productNumber), stock);
    }

    private String resolveName(int productNumber) {
        return switch (productNumber) {
            case 1 -> "Laptop";
            case 2 -> "Keyboard";
            case 3 -> "Mouse";
            default -> "Generic product";
        };
    }
}
