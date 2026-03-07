package lab.productservice.service;

import lab.productservice.client.StockClient;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

@Service
public class StockLookupService {

    private static final int FALLBACK_STOCK = -1;

    private final StockClient stockClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public StockLookupService(StockClient stockClient, CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.stockClient = stockClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public int fetchStock(int productNumber) {
        return circuitBreakerFactory.create("stockService")
                .run(() -> stockClient.getStock(productNumber), throwable -> FALLBACK_STOCK);
    }
}
