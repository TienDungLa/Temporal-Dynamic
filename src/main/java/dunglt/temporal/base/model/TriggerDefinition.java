package dunglt.temporal.base.model;

import lombok.Data;

@Data
public class TriggerDefinition {
    private String workflowType;
    private String sendMethod;
    private String path;
    private String topic;
}
