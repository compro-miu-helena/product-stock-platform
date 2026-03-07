package lab.productservice.service;

import feign.FeignException;
import lab.productservice.client.StockClient;
import lab.productservice.dto.ProductRequest;
import lab.productservice.model.ProductCommand;
import lab.productservice.model.ProductEvent;
import lab.productservice.model.ProductEventType;
import lab.productservice.model.ProductView;
import lab.productservice.repository.ProductCommandRepository;
import lab.productservice.repository.ProductQueryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductCommandService {

    private final ProductCommandRepository productCommandRepository;
    private final ProductQueryRepository productQueryRepository;
    private final StockClient stockClient;

    public ProductCommandService(ProductCommandRepository productCommandRepository,
                                 ProductQueryRepository productQueryRepository,
                                 StockClient stockClient) {
        this.productCommandRepository = productCommandRepository;
        this.productQueryRepository = productQueryRepository;
        this.stockClient = stockClient;
    }

    public ProductCommand addProduct(ProductRequest request) {
        if (currentProduct(request.productNumber()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Product already exists for productNumber " + request.productNumber());
        }

        ProductEvent savedEvent = productCommandRepository.save(new ProductEvent(
                null,
                request.productNumber(),
                nextSequenceNumber(request.productNumber()),
                ProductEventType.CREATED,
                request.name(),
                request.price()));

        ProductCommand savedProduct = toCommand(savedEvent);

        productQueryRepository.save(new ProductView(
                null,
                savedProduct.productNumber(),
                savedProduct.name(),
                savedProduct.price(),
                currentQuantity(savedProduct.productNumber())));

        return savedProduct;
    }

    public ProductCommand updateProduct(int productNumber, ProductRequest request) {
        requireCurrentProduct(productNumber);

        ProductEvent savedEvent = productCommandRepository.save(new ProductEvent(
                null,
                productNumber,
                nextSequenceNumber(productNumber),
                ProductEventType.UPDATED,
                request.name(),
                request.price()));

        ProductCommand savedProduct = toCommand(savedEvent);

        productQueryRepository.findByProductNumber(productNumber)
                .ifPresentOrElse(existingView -> productQueryRepository.save(new ProductView(
                                existingView.id(),
                                savedProduct.productNumber(),
                                savedProduct.name(),
                                savedProduct.price(),
                                existingView.numberInStock())),
                        () -> productQueryRepository.save(new ProductView(
                                null,
                                savedProduct.productNumber(),
                                savedProduct.name(),
                                savedProduct.price(),
                                currentQuantity(savedProduct.productNumber()))));

        return savedProduct;
    }

    public void deleteProduct(int productNumber) {
        requireCurrentProduct(productNumber);
        productCommandRepository.save(new ProductEvent(
                null,
                productNumber,
                nextSequenceNumber(productNumber),
                ProductEventType.DELETED,
                null,
                0.0));
        productQueryRepository.deleteByProductNumber(productNumber);
    }

    private ProductCommand requireCurrentProduct(int productNumber) {
        ProductCommand product = currentProduct(productNumber);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Product not found for productNumber " + productNumber);
        }
        return product;
    }

    private ProductCommand currentProduct(int productNumber) {
        List<ProductEvent> events = productCommandRepository.findByProductNumberOrderBySequenceNumberAsc(productNumber);
        if (events.isEmpty()) {
            return null;
        }

        ProductCommand current = null;
        for (ProductEvent event : events) {
            if (event.eventType() == ProductEventType.DELETED) {
                current = null;
            } else {
                current = toCommand(event);
            }
        }
        return current;
    }

    private long nextSequenceNumber(int productNumber) {
        List<ProductEvent> events = productCommandRepository.findByProductNumberOrderBySequenceNumberAsc(productNumber);
        if (events.isEmpty()) {
            return 1L;
        }
        return events.get(events.size() - 1).sequenceNumber() + 1;
    }

    private ProductCommand toCommand(ProductEvent event) {
        return new ProductCommand(event.productNumber(), event.name(), event.price());
    }

    private int currentQuantity(int productNumber) {
        try {
            return stockClient.getStock(productNumber).quantity();
        } catch (FeignException.NotFound exception) {
            return 0;
        }
    }
}
