package elasticsearch.example.demo.service;

import elasticsearch.example.demo.entity.Product;
import elasticsearch.example.demo.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpringDataProductServiceImpl implements SpringDataProductService {

  private final ProductRepository productRepository;

  public Product createProduct(Product product) {
    return productRepository.save(product);
  }

  public Optional<Product> getProduct(String id) {
    return productRepository.findById(id);
  }

  public void deleteProduct(String id) {
    productRepository.deleteById(id);
  }

  public Iterable<Product> insertBulk(List<Product> products) {
    return productRepository.saveAll(products);
  }

  public List<Product> getProductsByName(String name) {
    return productRepository.findAllByName(name);
  }

  public List<Product> getProductsByNameUsingAnnotation(String name) {
    return productRepository.findAllByNameUsingAnnotations(name);
  }

}
