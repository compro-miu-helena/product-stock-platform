package lab.productservice.service;

import java.util.List;

import lab.productservice.model.ProductView;
import lab.productservice.repository.ProductQueryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductQueryService {

    private final ProductQueryRepository productQueryRepository;

    public ProductQueryService(ProductQueryRepository productQueryRepository) {
        this.productQueryRepository = productQueryRepository;
    }

    public List<ProductView> getProducts() {
        return productQueryRepository.findAll();
    }

    public ProductView getProduct(int productNumber) {
        return productQueryRepository.findByProductNumber(productNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product not found for productNumber " + productNumber));
    }

    public ProductView updateStock(int productNumber, int quantity) {
        ProductView existingView = productQueryRepository.findByProductNumber(productNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product not found for productNumber " + productNumber));

        return productQueryRepository.save(new ProductView(
                existingView.id(),
                existingView.productNumber(),
                existingView.name(),
                existingView.price(),
                quantity));
    }
}
