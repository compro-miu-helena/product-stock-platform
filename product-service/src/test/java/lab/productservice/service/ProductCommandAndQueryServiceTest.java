package lab.productservice.service;

import lab.productservice.client.StockClient;
import lab.productservice.dto.ProductRequest;
import lab.productservice.dto.StockResponse;
import lab.productservice.model.ProductCommand;
import lab.productservice.model.ProductEvent;
import lab.productservice.model.ProductEventType;
import lab.productservice.model.ProductView;
import lab.productservice.repository.ProductCommandRepository;
import lab.productservice.repository.ProductQueryRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
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
        List<ProductEvent> eventStore = new ArrayList<>();
        AtomicReference<ProductView> savedView = new AtomicReference<>();

        ProductCommandService productCommandService = new ProductCommandService(
                productCommandRepository, productQueryRepository, stockClient);
        ProductQueryService productQueryService = new ProductQueryService(productQueryRepository);

        when(productCommandRepository.save(any(ProductEvent.class)))
                .thenAnswer(invocation -> {
                    ProductEvent event = invocation.getArgument(0);
                    ProductEvent persisted = new ProductEvent(
                            "evt-" + (eventStore.size() + 1),
                            event.productNumber(),
                            event.sequenceNumber(),
                            event.eventType(),
                            event.name(),
                            event.price());
                    eventStore.add(persisted);
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
        when(productCommandRepository.findByProductNumberOrderBySequenceNumberAsc(1001))
                .thenAnswer(invocation -> new ArrayList<>(eventStore));
        when(productQueryRepository.findByProductNumber(1001))
                .thenAnswer(invocation -> java.util.Optional.ofNullable(savedView.get()));

        ProductCommand createdProduct = productCommandService.addProduct(new ProductRequest(1001, "Monitor", 249.99));
        productQueryService.updateStock(1001, 12);

        assertThat(createdProduct.productNumber()).isEqualTo(1001);
        assertThat(createdProduct.name()).isEqualTo("Monitor");
        assertThat(createdProduct.price()).isEqualTo(249.99);
        assertThat(eventStore).hasSize(1);
        assertThat(eventStore.get(0).eventType()).isEqualTo(ProductEventType.CREATED);

        ProductView product = productQueryService.getProduct(1001);
        assertThat(product.name()).isEqualTo("Monitor");
        assertThat(product.price()).isEqualTo(249.99);
        assertThat(product.numberInStock()).isEqualTo(12);

        ProductCommand updatedCommand = productCommandService.updateProduct(1001, new ProductRequest(1001, "4K Monitor", 299.99));
        productQueryService.updateStock(1001, 7);

        assertThat(updatedCommand.name()).isEqualTo("4K Monitor");
        assertThat(updatedCommand.price()).isEqualTo(299.99);
        assertThat(eventStore).hasSize(2);
        assertThat(eventStore.get(1).eventType()).isEqualTo(ProductEventType.UPDATED);
        assertThat(eventStore.get(1).sequenceNumber()).isEqualTo(2L);

        ProductView updatedProduct = productQueryService.getProduct(1001);
        assertThat(updatedProduct.name()).isEqualTo("4K Monitor");
        assertThat(updatedProduct.price()).isEqualTo(299.99);
        assertThat(updatedProduct.numberInStock()).isEqualTo(7);
    }

    @Test
    void shouldAppendDeleteEventAndRemoveProjection() {
        ProductCommandRepository productCommandRepository = mock(ProductCommandRepository.class);
        ProductQueryRepository productQueryRepository = mock(ProductQueryRepository.class);
        StockClient stockClient = mock(StockClient.class);
        List<ProductEvent> eventStore = new ArrayList<>();

        ProductCommandService productCommandService = new ProductCommandService(
                productCommandRepository, productQueryRepository, stockClient);

        when(productCommandRepository.save(any(ProductEvent.class)))
                .thenAnswer(invocation -> {
                    ProductEvent event = invocation.getArgument(0);
                    ProductEvent persisted = new ProductEvent(
                            "evt-" + (eventStore.size() + 1),
                            event.productNumber(),
                            event.sequenceNumber(),
                            event.eventType(),
                            event.name(),
                            event.price());
                    eventStore.add(persisted);
                    return persisted;
                });
        when(productCommandRepository.findByProductNumberOrderBySequenceNumberAsc(2002))
                .thenAnswer(invocation -> new ArrayList<>(eventStore));
        when(stockClient.getStock(2002)).thenReturn(new StockResponse(2002, 0));
        when(productQueryRepository.save(any(ProductView.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        productCommandService.addProduct(new ProductRequest(2002, "Tablet", 599.99));
        productCommandService.deleteProduct(2002);

        assertThat(eventStore).hasSize(2);
        assertThat(eventStore.get(0).eventType()).isEqualTo(ProductEventType.CREATED);
        assertThat(eventStore.get(1).eventType()).isEqualTo(ProductEventType.DELETED);
        assertThat(eventStore.get(1).sequenceNumber()).isEqualTo(2L);
    }
}
