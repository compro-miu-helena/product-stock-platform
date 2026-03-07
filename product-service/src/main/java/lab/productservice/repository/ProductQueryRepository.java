package lab.productservice.repository;

import java.util.Optional;

import lab.productservice.model.ProductView;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductQueryRepository extends MongoRepository<ProductView, String> {
    Optional<ProductView> findByProductNumber(int productNumber);

    void deleteByProductNumber(int productNumber);
}
