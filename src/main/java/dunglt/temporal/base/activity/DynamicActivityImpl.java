package dunglt.temporal.base.activity;

import dunglt.temporal.base.model.MActivity;
import dunglt.temporal.base.model.MKafkaConfig;
import dunglt.temporal.base.model.MRestConfig;
import dunglt.temporal.base.service.ConnectionService;
import dunglt.temporal.base.utility.HttpRestClient;
import dunglt.temporal.base.utility.KafkaClient;
import dunglt.temporal.base.utility.SpringContextBridge;
import dunglt.temporal.base.utility.TemporalConstant;
import io.temporal.activity.DynamicActivity;
import io.temporal.common.converter.EncodedValues;
import io.temporal.failure.ApplicationFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class DynamicActivityImpl implements DynamicActivity {
    private static final Logger logger = LoggerFactory.getLogger(DynamicActivityImpl.class);

    @Override
    public Object execute(EncodedValues args) {
        MActivity mActivity = args.get(0, MActivity.class);
        Object data = args.get(1, Object.class);
        Map<String, Object> activityData = new HashMap<>();
        ConnectionService connectionService = SpringContextBridge.getBean(ConnectionService.class);


        try{
            logger.info("Executing activity: {}", mActivity.getActivityType());

            if (!StringUtils.hasText(mActivity.getSendMethod())){
                //TODO: throw error
            }

            switch (mActivity.getSendMethod()) {
                case "Rest":
                    MRestConfig restConfig = connectionService
                            .getRestConfigByActivityIdAndType(mActivity.getActivityId(), TemporalConstant.REST_CONFIG_TYPE_SEND);
                    HttpRestClient restClient = SpringContextBridge.getBean(HttpRestClient.class);
                    Object response = restClient.sendRequest(data, restConfig);
                    activityData.put("firstResponseData", response);
                    break;
                case "MQ":
                    break;
                case "WH":
                    break;
            }

            // Handle response method if specified(rcm is mq), this response data will be used in next activity if needed
            if(StringUtils.hasText(mActivity.getResponseMethod())){
                    switch (mActivity.getResponseMethod()) {
                        case "Rest":
                            break;
                        case "MQ":
                            MKafkaConfig config = connectionService
                                    .getKafkaConfigByActivityIdAndType(mActivity.getActivityId()
                                            , TemporalConstant.MQ_CONFIG_TYPE_RESPONSE);
                            KafkaClient client = SpringContextBridge.getBean(KafkaClient.class);
                            Object responseData = client.waitForMessage(config);
                            activityData.put("responseData", responseData);
                            break;
                    }
            }

        }catch (ApplicationFailure applicationFailure){
            logger.error("Application failure executing activity: {}", mActivity.getActivityType(), applicationFailure);
            throw applicationFailure;
        }catch (Exception e){
            logger.error("Error executing activity: {}", mActivity.getActivityType(), e);
            throw ApplicationFailure.newFailure(e.getMessage(), "ActivityExecutionFailure");
        }

        // validate data step before return

        if (mActivity.getIsFirstResponseData()){
            activityData.put("responseData", activityData.get("firstResponseData"));
        }

        if (activityData.get("responseData") == null){
            activityData.put("responseData", defaultResponseData());
        }


        /* the result map of process will contain
        - firstResponseData: the data that is returned from the first send method execution, normally is a status from
        first step
        - responseData: the data that will is returned form second step in this method
         */
        return activityData;
    }

    private Object defaultResponseData(){
        return "default response data";
    }
}
