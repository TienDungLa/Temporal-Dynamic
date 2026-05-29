package dunglt.temporal.base.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "m_rest_config")
public class MRestConfig {
    @Id
    @Column(name = "rest_config_id")
    private Integer restConfigId;

    @Column(name = "activity_id")
    private Integer activityId;

    // Type of REST call: send, response, notify
    @Column(name = "type")
    private String type;

    // Option for send
    @Column(name = "url")
    private String  url;

    @Column(name = "http_method")
    private String httpMethod; // GET, POST, PUT, DELETE, etc.

    @Column(name = "content_type")
    private String contentType; // application/json, application/xml, etc.

    @Column(name = "headers")
    private String headers; // JSON string of headers

    // Api key, token, username/password, ...
    @Column(name = "security_type")
    private String securityType;

    @Column(name = "security_value")
    private String securityValue; // The actual token/api key/password value

    // Option for response
}
