package lab.productservice.service;

import feign.FeignException;
import lab.productservice.client.StockClient;
import lab.productservice.dto.ProductRequest;
import lab.productservice.model.ProductCommand;
import lab.productservice.model.ProductView;
import lab.productservice.repository.ProductCommandRepository;
import lab.productservice.repository.ProductQueryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        if (productCommandRepository.existsByProductNumber(request.productNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Product already exists for productNumber " + request.productNumber());
        }

        ProductCommand savedProduct = productCommandRepository.save(
                new ProductCommand(null, request.productNumber(), request.name(), request.price()));

        productQueryRepository.save(new ProductView(
                null,
                savedProduct.productNumber(),
                savedProduct.name(),
                savedProduct.price(),
                currentQuantity(savedProduct.productNumber())));

        return savedProduct;
    }

    public ProductCommand updateProduct(int productNumber, ProductRequest request) {
        ProductCommand existingProduct = productCommandRepository.findByProductNumber(productNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product not found for productNumber " + productNumber));

        ProductCommand savedProduct = productCommandRepository.save(new ProductCommand(
                existingProduct.id(),
                productNumber,
                request.name(),
                request.price()));

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
        ProductCommand existingProduct = productCommandRepository.findByProductNumber(productNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product not found for productNumber " + productNumber));

        productCommandRepository.delete(existingProduct);
        productQueryRepository.deleteByProductNumber(productNumber);
    }

    private int currentQuantity(int productNumber) {
        try {
            return stockClient.getStock(productNumber).quantity();
        } catch (FeignException.NotFound exception) {
            return 0;
        }
    }
}
