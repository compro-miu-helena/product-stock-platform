package lab.productservice.controller;

import java.util.List;

import lab.productservice.model.ProductView;
import lab.productservice.service.ProductQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductQueryController {

    private final ProductQueryService productQueryService;

    public ProductQueryController(ProductQueryService productQueryService) {
        this.productQueryService = productQueryService;
    }

    @GetMapping
    public List<ProductView> getProducts() {
        return productQueryService.getProducts();
    }

    @GetMapping("/{productNumber}")
    public ProductView getProduct(@PathVariable int productNumber) {
        return productQueryService.getProduct(productNumber);
    }
}
