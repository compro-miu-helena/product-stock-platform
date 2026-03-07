package lab.productservice.controller;

import lab.productservice.model.Product;
import lab.productservice.service.ProductCatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductCatalogService productCatalogService;

    public ProductController(ProductCatalogService productCatalogService) {
        this.productCatalogService = productCatalogService;
    }

    @GetMapping("/{productNumber}")
    public Product getProduct(@PathVariable int productNumber) {
        return productCatalogService.getProduct(productNumber);
    }
}
