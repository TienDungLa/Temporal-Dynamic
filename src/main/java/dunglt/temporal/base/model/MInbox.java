package dunglt.temporal.base.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "m_inbox")
public class MInbox {

    public static final String STATUS_CREATED = "CREATED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_NOTIFIED = "NOTIFIED";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "inbox_id")
    private Integer inboxId;

    @Column(name = "workflow_id")
    private Integer workflowId;

    @Column(name = "activity_id")
    private Integer activityId;

    @Column(name = "status")
    private String status;
}
