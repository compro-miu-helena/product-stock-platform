package lab.productservice.service;

import lab.productservice.client.StockClient;
import lab.productservice.dto.ProductRequest;
import lab.productservice.dto.StockResponse;
import lab.productservice.model.ProductCommand;
import lab.productservice.model.ProductView;
import lab.productservice.repository.ProductCommandRepository;
import lab.productservice.repository.ProductQueryRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductCommandAndQueryServiceTest {

    @Test
    void shouldReflectProductAndStockChangesInProductQueryService() {
        ProductCommandRepository productCommandRepository = mock(ProductCommandRepository.class);
        ProductQueryRepository productQueryRepository = mock(ProductQueryRepository.class);
        StockClient stockClient = mock(StockClient.class);
        AtomicReference<ProductCommand> savedCommand = new AtomicReference<>();
        AtomicReference<ProductView> savedView = new AtomicReference<>();

        ProductCommandService productCommandService = new ProductCommandService(
                productCommandRepository, productQueryRepository, stockClient);
        ProductQueryService productQueryService = new ProductQueryService(productQueryRepository);

        when(productCommandRepository.existsByProductNumber(1001)).thenReturn(false);
        when(productCommandRepository.save(any(ProductCommand.class)))
                .thenAnswer(invocation -> {
                    ProductCommand command = invocation.getArgument(0);
                    ProductCommand persisted = new ProductCommand("cmd-1",
                            command.productNumber(),
                            command.name(),
                            command.price());
                    savedCommand.set(persisted);
                    return persisted;
                });
        when(productQueryRepository.save(any(ProductView.class)))
                .thenAnswer(invocation -> {
                    ProductView view = invocation.getArgument(0);
                    ProductView persisted = new ProductView("view-1",
                            view.productNumber(),
                            view.name(),
                            view.price(),
                            view.numberInStock());
                    savedView.set(persisted);
                    return persisted;
                });
        when(stockClient.getStock(1001))
                .thenReturn(new StockResponse(1001, 0), new StockResponse(1001, 7));
        when(productCommandRepository.findByProductNumber(1001))
                .thenAnswer(invocation -> Optional.ofNullable(savedCommand.get()));
        when(productQueryRepository.findByProductNumber(1001))
                .thenAnswer(invocation -> Optional.ofNullable(savedView.get()));

        productCommandService.addProduct(new ProductRequest(1001, "Monitor", 249.99));
        productQueryService.updateStock(1001, 12);

        ProductView product = productQueryService.getProduct(1001);
        assertThat(product.name()).isEqualTo("Monitor");
        assertThat(product.price()).isEqualTo(249.99);
        assertThat(product.numberInStock()).isEqualTo(12);

        productCommandService.updateProduct(1001, new ProductRequest(1001, "4K Monitor", 299.99));
        productQueryService.updateStock(1001, 7);

        ProductView updatedProduct = productQueryService.getProduct(1001);
        assertThat(updatedProduct.name()).isEqualTo("4K Monitor");
        assertThat(updatedProduct.price()).isEqualTo(299.99);
        assertThat(updatedProduct.numberInStock()).isEqualTo(7);
    }
}
