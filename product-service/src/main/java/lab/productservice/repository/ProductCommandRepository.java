package lab.productservice.repository;

import java.util.Optional;

import lab.productservice.model.ProductCommand;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductCommandRepository extends MongoRepository<ProductCommand, String> {
    Optional<ProductCommand> findByProductNumber(int productNumber);

    boolean existsByProductNumber(int productNumber);
}
