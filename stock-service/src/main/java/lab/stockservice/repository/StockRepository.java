package lab.stockservice.repository;

import java.util.Optional;

import lab.stockservice.model.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockRepository extends MongoRepository<Stock, String> {
    Optional<Stock> findByProductNumber(int productNumber);

    boolean existsByProductNumber(int productNumber);
}
