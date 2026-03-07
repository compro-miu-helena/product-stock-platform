package lab.stockservice;

import lab.stockservice.dto.StockRequest;
import lab.stockservice.model.Stock;
import lab.stockservice.service.StockCommandService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stock")
public class StockController {

    private final StockCommandService stockCommandService;

    public StockController(StockCommandService stockCommandService) {
        this.stockCommandService = stockCommandService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Stock addStock(@RequestBody StockRequest request) {
        return stockCommandService.addStock(request);
    }

    @PutMapping("/{productNumber}")
    public Stock updateStock(@PathVariable int productNumber, @RequestBody StockRequest request) {
        return stockCommandService.updateStock(productNumber, request);
    }

    @DeleteMapping("/{productNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStock(@PathVariable int productNumber) {
        stockCommandService.deleteStock(productNumber);
    }

    @GetMapping("/{productNumber}")
    public Stock getStock(@PathVariable int productNumber) {
        return stockCommandService.getStock(productNumber);
    }
}
