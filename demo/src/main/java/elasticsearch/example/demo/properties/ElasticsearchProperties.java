package elasticsearch.example.demo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

@Data
@ConfigurationProperties("elasticsearch")
public class ElasticsearchProperties {
    private String[] hostAndPort;
    private Duration socketTimeout;
    private Duration connectTimeout;
}
