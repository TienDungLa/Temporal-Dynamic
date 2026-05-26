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
    private String workflowId;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "activity_sequence")
    private Integer activitySequence;

    @Column(name = "status")
    private String status;

    @Column(name = "send_payload", columnDefinition = "TEXT")
    private String sendPayload;

    @Column(name = "response_payload", columnDefinition = "TEXT")
    private String responsePayload;

    @Column(name = "notify_payload", columnDefinition = "TEXT")
    private String notifyPayload;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}
