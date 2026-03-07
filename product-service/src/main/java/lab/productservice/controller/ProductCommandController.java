package lab.productservice.controller;

import lab.productservice.dto.ProductRequest;
import lab.productservice.model.ProductCommand;
import lab.productservice.service.ProductCommandService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductCommandController {

    private final ProductCommandService productCommandService;

    public ProductCommandController(ProductCommandService productCommandService) {
        this.productCommandService = productCommandService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductCommand addProduct(@RequestBody ProductRequest request) {
        return productCommandService.addProduct(request);
    }

    @PutMapping("/{productNumber}")
    public ProductCommand updateProduct(@PathVariable int productNumber,
                                        @RequestBody ProductRequest request) {
        return productCommandService.updateProduct(productNumber, request);
    }

    @DeleteMapping("/{productNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable int productNumber) {
        productCommandService.deleteProduct(productNumber);
    }
}
