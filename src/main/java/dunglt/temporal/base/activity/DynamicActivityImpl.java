package dunglt.temporal.base.activity;

import dunglt.temporal.base.model.MActivity;
import io.temporal.activity.DynamicActivity;
import io.temporal.common.converter.EncodedValues;
import io.temporal.failure.ApplicationFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class DynamicActivityImpl implements DynamicActivity {
    private static final Logger logger = LoggerFactory.getLogger(DynamicActivityImpl.class);

    @Override
    public Object execute(EncodedValues args) {
        MActivity mActivity = args.get(0, MActivity.class);

        try{
            logger.info("Executing activity: {}", mActivity.getActivityType());

            if (mActivity.getSendMethod().equals("Rest")){

            }

            if(StringUtils.hasText(mActivity.getResponseMethod())){

            }

        }catch (ApplicationFailure applicationFailure){
            logger.error("Application failure executing activity: {}", mActivity.getActivityType(), applicationFailure);
            throw applicationFailure;
        }catch (Exception e){
            logger.error("Error executing activity: {}", mActivity.getActivityType(), e);
            throw ApplicationFailure.newFailure(e.getMessage(), "ActivityExecutionFailure");
        }

        return null;
    }

}
