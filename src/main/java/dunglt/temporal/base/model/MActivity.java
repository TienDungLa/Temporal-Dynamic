package dunglt.temporal.base.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "m_activity")
public class MActivity {

    @Id
    @Column(name = "activity_id")
    private Integer activityId;

    @Column(name = "activity_type")
    private String activityType;

    @Column(name = "activity_description")
    private String activityDescription;

    @Column(name = "workflow_id")
    private Integer workflowId;

    @Column(name = "sequenceNo")
    private Integer sequenceNo;

    @Column(name = "retry_attempt")
    private Integer retryAttempt;

    // method: Rest, MQ, Websocket
    @Column(name = "send_method")
    private String sendMethod;

    // method: Rest, MQ, Websocket
    @Column(name = "response_method")
    private String responseMethod;

    @Column(name = "notify_method")
    private String notifyMethod;

    //url để gửi request từ orches đến service
    @Column(name = "send_url")
    private String sendUrl;

    // url để response từ service trả về orches (nếu có)
    @Column(name = "response_url")
    private String responseUrl;

    @Column(name = "notify_url")
    private String notifyUrl;
}
