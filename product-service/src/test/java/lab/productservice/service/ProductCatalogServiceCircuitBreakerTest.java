package lab.productservice.service;

import lab.productservice.client.StockClient;
import lab.productservice.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "spring.cloud.consul.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
class ProductCatalogServiceCircuitBreakerTest {

    @Autowired
    private ProductCatalogService productCatalogService;

    @MockBean
    private StockClient stockClient;

    @Test
    void shouldReturnFallbackAndOpenCircuitAfterRepeatedFailures() {
        when(stockClient.getStock(anyInt())).thenThrow(new RuntimeException("stock-service unavailable"));

        Product firstResponse = productCatalogService.getProduct(1);
        Product secondResponse = productCatalogService.getProduct(1);
        Product thirdResponse = productCatalogService.getProduct(1);

        assertThat(firstResponse.numberOnStock()).isEqualTo(-1);
        assertThat(secondResponse.numberOnStock()).isEqualTo(-1);
        assertThat(thirdResponse.numberOnStock()).isEqualTo(-1);
        verify(stockClient, times(2)).getStock(1);
    }
}
