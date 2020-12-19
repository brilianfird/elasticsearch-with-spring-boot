package elasticsearch.example.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import elasticsearch.example.demo.entity.Product;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.elasticsearch.core.index.MappingBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HighLevelClientProductServiceImpl implements HighLevelClientProductService {

  private final RestHighLevelClient restHighLevelClient;
  private final ObjectMapper objectMapper;

  public Product createProduct(Product product) {
    IndexRequest indexRequest = new IndexRequest("product");
    indexRequest.id(product.getId());
    indexRequest.source(product);

    try {
      IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
      if (indexResponse.status() == RestStatus.ACCEPTED) {
        return product;
      }

      throw new RuntimeException("Wrong status: " + indexResponse.status());
    } catch (Exception e) {
      log.error("Error indexing, product: {}", product, e);
      return null;
    }
  }

  public List<Product> bulkInsert(List<Product> products) {
    BulkRequest bulkRequest = new BulkRequest();
    products
        .forEach(product -> {
          IndexRequest indexRequest = new IndexRequest("product");
          indexRequest.id(product.getId());
          bulkRequest.add(indexRequest);
        });

    try {
      BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
      if (!bulk.hasFailures()) {
        return products;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new ArrayList<>();
  }

  public Product getProduct(String id) {
    SearchRequest searchRequest = new SearchRequest("product");
    SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();
    IdsQueryBuilder idsQueryBuilder = QueryBuilders.idsQuery().addIds(id);
    sourceBuilder.query(idsQueryBuilder);
    searchRequest.source(sourceBuilder);
    try {
      SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
      return toProductList(response.getHits().getHits()).stream().findFirst().orElse(null);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public boolean deleteProduct(String id) {
    DeleteRequest deleteRequest = new DeleteRequest();
    deleteRequest.id(id);
    try {
      DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
      if (deleteResponse.status() == RestStatus.ACCEPTED) {
        return true;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return false;
  }

  private List<Product> toProductList(SearchHit[] searchHits) throws Exception {
    List<Product> productList = new ArrayList<>();
    for (SearchHit searchHit : searchHits) {
      productList.add(objectMapper.readValue(searchHit.getSourceAsString(), Product.class));
    }
    return productList;
  }

  public Map<String, Long> aggregateTerm(String term) {
    SearchRequest searchRequest = new SearchRequest("product");
    SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();
    TermsAggregationBuilder terms = AggregationBuilders.terms(term).field(term);

    sourceBuilder.size(0);
    sourceBuilder.aggregation(terms);

    searchRequest.source(sourceBuilder);
    try {
      Map<String, Long> result = new HashMap<>();
      SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
      List<Aggregation> aggregations = response.getAggregations().asList();
      aggregations
          .forEach(aggregation -> {
            ((Terms) aggregation).getBuckets()
                .forEach(bucket -> result.put(bucket.getKeyAsString(), bucket.getDocCount()));
          });
      return result;
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }


  public boolean createProductIndex() {
    CreateIndexRequest createIndexRequest = new CreateIndexRequest("product");
    createIndexRequest.settings(Settings.builder()
        .put("number_of_shards", 1)
        .put("number_of_replicas", 0)
        .build());
    Map<String, Map<String, String>> mappings = new HashMap<>();

    mappings.put("name", Collections.singletonMap("type", "text"));
    mappings.put("category", Collections.singletonMap("type", "keyword"));
    mappings.put("price", Collections.singletonMap("type", "long"));
    createIndexRequest.mapping(Collections.singletonMap("properties", mappings));
    try {
      CreateIndexResponse createIndexResponse = restHighLevelClient.indices()
          .create(createIndexRequest, RequestOptions.DEFAULT);
      return createIndexResponse.isAcknowledged();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

}

