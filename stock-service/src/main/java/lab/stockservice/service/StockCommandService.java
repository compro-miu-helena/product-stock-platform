package lab.stockservice.service;

import feign.FeignException;
import lab.stockservice.client.ProductQuerySyncClient;
import lab.stockservice.dto.StockRequest;
import lab.stockservice.dto.StockUpdateRequest;
import lab.stockservice.model.Stock;
import lab.stockservice.repository.StockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StockCommandService {

    private final StockRepository stockRepository;
    private final ProductQuerySyncClient productQuerySyncClient;

    public StockCommandService(StockRepository stockRepository, ProductQuerySyncClient productQuerySyncClient) {
        this.stockRepository = stockRepository;
        this.productQuerySyncClient = productQuerySyncClient;
    }

    public Stock addStock(StockRequest request) {
        if (stockRepository.existsByProductNumber(request.productNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Stock already exists for productNumber " + request.productNumber());
        }

        Stock savedStock = stockRepository.save(new Stock(null, request.productNumber(), request.quantity()));
        syncProductQuery(savedStock.productNumber(), savedStock.quantity());
        return savedStock;
    }

    public Stock updateStock(int productNumber, StockRequest request) {
        Stock existingStock = stockRepository.findByProductNumber(productNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Stock not found for productNumber " + productNumber));

        Stock savedStock = stockRepository.save(new Stock(existingStock.id(), productNumber, request.quantity()));
        syncProductQuery(savedStock.productNumber(), savedStock.quantity());
        return savedStock;
    }

    public void deleteStock(int productNumber) {
        Stock existingStock = stockRepository.findByProductNumber(productNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Stock not found for productNumber " + productNumber));

        stockRepository.delete(existingStock);
        syncProductQuery(productNumber, 0);
    }

    public Stock getStock(int productNumber) {
        return stockRepository.findByProductNumber(productNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Stock not found for productNumber " + productNumber));
    }

    private void syncProductQuery(int productNumber, int quantity) {
        try {
            productQuerySyncClient.updateStock(productNumber, new StockUpdateRequest(quantity));
        } catch (FeignException.NotFound exception) {
            // Stock can be created before the product projection exists.
        }
    }
}
