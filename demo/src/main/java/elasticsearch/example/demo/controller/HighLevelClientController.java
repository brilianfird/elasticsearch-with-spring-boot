package elasticsearch.example.demo.controller;

import elasticsearch.example.demo.entity.Product;
import elasticsearch.example.demo.service.HighLevelClientProductService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/high-level-client")
@RequiredArgsConstructor
public class HighLevelClientController {

  private final HighLevelClientProductService highLevelClientProductService;

  @GetMapping("{id}")
  public Product getProductById(@PathVariable String id) {
    return highLevelClientProductService.getProduct(id);
  }

  @GetMapping("aggregate/{term}")
  public Map<String, Long> aggregateByTerms(@PathVariable String term) {
    return highLevelClientProductService.aggregateTerm(term);
  }

  @PostMapping("/product/_create-index")
  public boolean createIndex(){
    return highLevelClientProductService.createProductIndex();
  }


}
