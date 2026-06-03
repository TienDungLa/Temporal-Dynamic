package dunglt.temporal.base.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "temporal")
@Getter
@Setter
public class TemporalProperties {
    private String host;
    private int port;
    private String namespace;

}