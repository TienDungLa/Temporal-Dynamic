package dunglt.temporal.base.activity;

import dunglt.temporal.base.model.MActivity;
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
        Map<String, Object> activityData = new HashMap<>();


        try{
            logger.info("Executing activity: {}", mActivity.getActivityType());

            if (mActivity.getSequenceNo().equals(2)){
                throw ApplicationFailure.newFailure("Simulated failure in activity " + mActivity.getActivityType(), "SimulatedFailure");
            }

            if (StringUtils.hasText(mActivity.getSendMethod())){

            }

            // Handle response method if specified(rcm is wh), this response data will be used in next activity if needed
            if(StringUtils.hasText(mActivity.getResponseMethod())){

            }

        }catch (ApplicationFailure applicationFailure){
            logger.error("Application failure executing activity: {}", mActivity.getActivityType(), applicationFailure);
            throw applicationFailure;
        }catch (Exception e){
            logger.error("Error executing activity: {}", mActivity.getActivityType(), e);
            throw ApplicationFailure.newFailure(e.getMessage(), "ActivityExecutionFailure");
        }

        activityData.put("responseData", "response data from activity " + mActivity.getSequenceNo());

        return activityData;
    }

}
