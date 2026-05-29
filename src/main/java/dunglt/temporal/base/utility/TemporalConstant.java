package dunglt.temporal.base.utility;

public class TemporalConstant {

    //Inbox status
    public static final String INBOX_STATUS_CREATED = "CREATED";
    public static final String INBOX_STATUS_COMPLETED = "COMPLETED";
    public static final String INBOX_STATUS_FAILED = "FAILED";
    public static final String INBOX_STATUS_PROCESSING = "PROCESSING";
    public static final String INBOX_STATUS_NOTIFIED = "NOTIFIED";
    public static final String INBOX_STATUS_COMPENSATION = "COMPENSATION";


    //Notification type
    public static final String NOTIFY_TYPE_COMPLETED = "COMPLETED";
    public static final String NOTIFY_TYPE_COMPENSATION = "COMPENSATION";
    public static final String NOTIFY_SEND_COMPLETE = "SEND_COMPLETE";
    public static final String NOTIFY_SEND_FAILED = "SEND_FAILED";

    public static final String REST_CONFIG_TYPE_SEND = "SEND";
    public static final String REST_CONFIG_TYPE_NOTIFY = "NOTIFY";

    public static final String MQ_CONFIG_TYPE_SEND = "SEND";
    public static final String MQ_CONFIG_TYPE_RESPONSE = "RESPONSE";
    public static final String MQ_CONFIG_TYPE_NOTIFY = "NOTIFY";
}
