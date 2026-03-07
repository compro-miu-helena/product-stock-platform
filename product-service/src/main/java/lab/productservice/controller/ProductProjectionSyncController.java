package lab.productservice.controller;

import lab.productservice.dto.StockUpdateRequest;
import lab.productservice.model.ProductView;
import lab.productservice.service.ProductQueryService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/products")
public class ProductProjectionSyncController {

    private final ProductQueryService productQueryService;

    public ProductProjectionSyncController(ProductQueryService productQueryService) {
        this.productQueryService = productQueryService;
    }

    @PutMapping("/{productNumber}/stock")
    public ProductView updateStock(@PathVariable int productNumber,
                                   @RequestBody StockUpdateRequest request) {
        return productQueryService.updateStock(productNumber, request.quantity());
    }
}
