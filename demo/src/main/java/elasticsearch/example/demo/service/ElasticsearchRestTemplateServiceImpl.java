package elasticsearch.example.demo.service;

import elasticsearch.example.demo.entity.Product;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchRestTemplateServiceImpl implements ElasticsearchRestTemplateService {

  private final ElasticsearchRestTemplate elasticsearchRestTemplate;

  public Product getProductById(String id) {

    SearchHits<Product> searchHits = elasticsearchRestTemplate
        .search(new CriteriaQuery(new Criteria("id").is(id)), Product.class);
    return searchHits.get().map(SearchHit::getContent).findFirst().orElse(null);
  }

  public List<Product> getProductsByName(String name) {
    Query query = new NativeSearchQueryBuilder()
        .withQuery(QueryBuilders.matchQuery("name", name))
        .build();
    SearchHits<Product> searchHits = elasticsearchRestTemplate.search(query, Product.class);

    return searchHits.get().map(SearchHit::getContent).collect(Collectors.toList());
  }

  public Map<String, Long> aggregateTerm(String term) {
    Query query = new NativeSearchQueryBuilder()
        .addAggregation(new TermsAggregationBuilder(term).field(term).size(10))
        .build();

    SearchHits<Product> searchHits = elasticsearchRestTemplate.search(query, Product.class);
    Map<String, Long> result = new HashMap<>();
    searchHits.getAggregations().asList().forEach(aggregation -> {
      ((Terms) aggregation).getBuckets()
          .forEach(bucket -> result.put(bucket.getKeyAsString(), bucket.getDocCount()));
    });

    return result;
  }

}
