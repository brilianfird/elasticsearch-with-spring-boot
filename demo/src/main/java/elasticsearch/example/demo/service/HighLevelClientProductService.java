package elasticsearch.example.demo.service;

import elasticsearch.example.demo.entity.Product;
import java.util.List;
import java.util.Map;
import org.elasticsearch.search.aggregations.Aggregation;

public interface HighLevelClientProductService {

  Product getProduct(String id);

  Map<String, Long> aggregateTerm(String term);

  Product createProduct(Product product);

  boolean deleteProduct(String id);

  boolean createProductIndex();
}
