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

    @Column (name = "sendByRestApi")
    private boolean sendByRestApi;

    @Column (name = "responseByRestApi")
    private boolean responseByRestApi;

    //url để gửi request từ orches đến service
    @Column (name = "send_url")
    private String sendUrl;

    // url để service nhận trạng thái cuối của workflow
    @Column (name = "response_url")
    private String responseUrl;

    @Column (name = "notify_url")
    private String notifyUrl;
}
