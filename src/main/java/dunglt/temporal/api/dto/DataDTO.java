package dunglt.temporal.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DataDTO extends AbstractDTO {

    @JsonProperty("data")
    private Object data;
}
