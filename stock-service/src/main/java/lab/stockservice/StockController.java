package lab.stockservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stock")
public class StockController {

    @GetMapping("/{productNumber}")
    public int getStock(@PathVariable int productNumber) {
        return switch (productNumber) {
            case 1 -> 40;
            case 2 -> 15;
            case 3 -> 0;
            default -> 8;
        };
    }
}
