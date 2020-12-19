package elasticsearch.example.demo.repository;

import elasticsearch.example.demo.entity.Product;
import java.util.List;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductRepository extends ElasticsearchRepository<Product, String> {

  List<Product> findAllByName(String name);

  @Query("{\"match\":{\"name\":\"?0\"}}")
  List<Product> findAllByNameUsingAnnotations(String name);
}
