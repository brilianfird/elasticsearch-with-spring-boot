package elasticsearch.example.demo.controller;

import elasticsearch.example.demo.entity.Product;
import elasticsearch.example.demo.service.SpringDataProductService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spring-data")
@RequiredArgsConstructor
public class SpringDataController {

  private final SpringDataProductService springDataProductService;

  @PostMapping
  public Product createProduct(@RequestBody Product product) {
    return springDataProductService.createProduct(product);
  }

  @GetMapping("/{id}")
  public Optional<Product> getById(@PathVariable String id) {
    return springDataProductService.getProduct(id);
  }

  @DeleteMapping("/{id}")
  public boolean deleteById(@PathVariable String id) {
    springDataProductService.deleteProduct(id);
    return true;
  }

  @PostMapping("/_bulk")
  public List<Product> insertBulk(@RequestBody List<Product> products) {
    return (List<Product>) springDataProductService.insertBulk(products);
  }

  @GetMapping("/name/{name}")
  public List<Product> findAllByName(@PathVariable String name) {
    return springDataProductService.getProductsByName(name);
  }

  @GetMapping("/name/{name}/annotations")
  public List<Product> findAllByNameAnnotations(@PathVariable String name) {
    return springDataProductService.getProductsByNameUsingAnnotation(name);
  }
}
