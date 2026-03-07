package lab.productservice.repository;

import java.util.List;

import lab.productservice.model.ProductEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductCommandRepository extends MongoRepository<ProductEvent, String> {
    List<ProductEvent> findByProductNumberOrderBySequenceNumberAsc(int productNumber);
}
