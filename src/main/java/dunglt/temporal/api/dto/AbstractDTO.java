package dunglt.temporal.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AbstractDTO {
    @JsonProperty("sourceSystem")
    private String sourceSystem;

    @JsonProperty("workflowType")
    @NotNull
    private String workflowType;
}
