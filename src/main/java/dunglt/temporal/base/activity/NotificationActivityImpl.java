package dunglt.temporal.base.activity;

import dunglt.temporal.base.model.MActivity;
import dunglt.temporal.base.model.MKafkaConfig;
import dunglt.temporal.base.model.MRestConfig;
import dunglt.temporal.base.service.ConnectionService;
import dunglt.temporal.base.utility.HttpRestClient;
import dunglt.temporal.base.utility.KafkaClient;
import dunglt.temporal.base.utility.SpringContextBridge;
import dunglt.temporal.base.utility.TemporalConstant;

import java.net.http.HttpClient;

public class NotificationActivityImpl implements INotificationActivity{

    @Override
    public String sendNotification(String typeNotify, MActivity mActivity) {
        ConnectionService connectionService = SpringContextBridge.getBean(ConnectionService.class);

        if (typeNotify.equals(TemporalConstant.NOTIFY_TYPE_COMPLETED)){
            if (mActivity.getNotifyMethod().equals(TemporalConstant.NOTIFY_METHOD_REST)){
                MRestConfig config = connectionService
                        .getRestConfigByActivityIdAndType(mActivity.getActivityId(), TemporalConstant.REST_CONFIG_TYPE_NOTIFY);
                HttpRestClient restClient = SpringContextBridge.getBean(HttpRestClient.class);
                String sendResult = restClient.sendRequest(defaultNotifyData(), config, null);
                if (sendResult == null){
                    return TemporalConstant.NOTIFY_SEND_FAILED;
                }
                return TemporalConstant.NOTIFY_SEND_COMPLETE;
            } else if (mActivity.getNotifyMethod().equals(TemporalConstant.NOTIFY_METHOD_MQ)) {
                MKafkaConfig config = connectionService
                        .getKafkaConfigByActivityIdAndType(mActivity.getActivityId(), TemporalConstant.MQ_CONFIG_TYPE_NOTIFY);
                KafkaClient client = SpringContextBridge.getBean(KafkaClient.class);
                String sendResult = client.sendMessage(defaultNotifyData(), config);
                if (sendResult == null){
                    return TemporalConstant.NOTIFY_SEND_FAILED;
                }
                return TemporalConstant.NOTIFY_SEND_COMPLETE;
            }
        }

        if (typeNotify.equals(TemporalConstant.NOTIFY_TYPE_COMPENSATION)) {
            if (mActivity.getNotifyMethod().equals(TemporalConstant.NOTIFY_METHOD_REST)) {
                MRestConfig config = connectionService
                        .getRestConfigByActivityIdAndType(mActivity.getActivityId(), TemporalConstant.REST_CONFIG_TYPE_NOTIFY);
                HttpRestClient restClient = SpringContextBridge.getBean(HttpRestClient.class);
                String sendResult = restClient.sendRequest(defaultNotifyData(), config, null);
                if (sendResult == null) {
                    return TemporalConstant.NOTIFY_SEND_FAILED;
                }
                return TemporalConstant.NOTIFY_SEND_COMPLETE;
            }
        }

        return "";
    }

    private Object defaultNotifyData(){
        return "default notify data";
    }
}
