package dunglt.temporal.base.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "m_inbox")
public class MInbox {

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

    @Column(name = "notify_result")
    private String notifyResult;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}
