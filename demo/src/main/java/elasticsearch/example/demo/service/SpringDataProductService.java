package elasticsearch.example.demo.service;

import elasticsearch.example.demo.entity.Product;
import java.util.List;
import java.util.Optional;

public interface SpringDataProductService {

  Product createProduct(Product product);

  Optional<Product> getProduct(String id);

  void deleteProduct(String id);

  Iterable<Product> insertBulk(List<Product> products);

  List<Product> getProductsByName(String name);

  List<Product> getProductsByNameUsingAnnotation(String name);
}
