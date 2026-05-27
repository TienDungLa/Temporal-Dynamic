package dunglt.temporal.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AbstractDTO {
    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("sourceSystem")
    private String sourceSystem;
}
