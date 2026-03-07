package lab.productservice.client;

import lab.productservice.dto.StockResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "stock-service")
public interface StockClient {

    @GetMapping("/stock/{productNumber}")
    StockResponse getStock(@PathVariable int productNumber);
}
