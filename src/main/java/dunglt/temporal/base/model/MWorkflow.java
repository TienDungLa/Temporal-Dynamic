package dunglt.temporal.base.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Duration;

@Data
@Entity
@Table(name = "m_workflow")
public class MWorkflow {

    @Id
    @Column(name = "workflow_id")
    private Integer workflowId;

    @Column(name = "workflow_type")
    private String workflowType;

    @Column(name = "workflow_description")
    private String workflowDescription;

    @Column(name = "isActive")
    private boolean isActive;

    @Column(name = "workflow_taskqueue")
    private String workflowTaskqueue;

    @Column(name = "isSagaPattern")
    private boolean isSagaPattern;



}
