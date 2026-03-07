package lab.stockservice.client;

import lab.stockservice.dto.StockUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service")
public interface ProductQuerySyncClient {

    @PutMapping("/internal/products/{productNumber}/stock")
    void updateStock(@PathVariable int productNumber, @RequestBody StockUpdateRequest request);
}
