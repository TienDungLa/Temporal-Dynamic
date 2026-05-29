package dunglt.temporal.base.utility;

import dunglt.temporal.base.model.MRestConfig;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class HttpRestClient {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String sendRequest(Object data, MRestConfig config){

        if (config == null){
            return null;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();

            // Build URL
            String url = config.getUrl();

            // Build request body
            String requestBody = objectMapper.writeValueAsString(data);

            // Build request
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url));

            // Set HTTP method and body
            String method = config.getHttpMethod() != null ? config.getHttpMethod().toUpperCase() : "POST";
            switch (method) {
                case "GET":
                    requestBuilder.GET();
                    break;
                case "POST":
                    requestBuilder.POST(HttpRequest.BodyPublishers.ofString(requestBody));
                    break;
                case "PUT":
                    requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(requestBody));
                    break;
                case "DELETE":
                    requestBuilder.DELETE();
                    break;
                default:
                    requestBuilder.POST(HttpRequest.BodyPublishers.ofString(requestBody));
            }

            // Set Content-Type
            if (config.getContentType() != null) {
                requestBuilder.header("Content-Type", config.getContentType());
            } else {
                requestBuilder.header("Content-Type", "application/json");
            }

            // Set custom headers
            if (config.getHeaders() != null && !config.getHeaders().isEmpty()) {
                try {
                    var headersMap = objectMapper.readValue(config.getHeaders(), java.util.Map.class);
                    headersMap.forEach((key, value) -> requestBuilder.header(key.toString(), value.toString()));
                } catch (Exception e) {
                    // Ignore invalid headers JSON
                }
            }

            // Set security/authentication
            //TODO:
            if (config.getSecurityType() != null && config.getSecurityValue() != null) {
                switch (config.getSecurityType().toUpperCase()) {
                    case "BEARER":
                        requestBuilder.header("Authorization", "Bearer " + config.getSecurityValue());
                        break;
                    case "APIKEY":
                        requestBuilder.header("API-Key", config.getSecurityValue());
                        break;
                    case "BASIC":
                        requestBuilder.header("Authorization", "Basic " + config.getSecurityValue());
                        break;
                    default:
                        requestBuilder.header("Authorization", config.getSecurityValue());
                }
            }

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException("Error sending HTTP request: " + e.getMessage(), e);
        }
    }
}
