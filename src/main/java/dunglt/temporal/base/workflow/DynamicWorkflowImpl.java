package dunglt.temporal.base.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import dunglt.temporal.api.dto.DataDTO;
import dunglt.temporal.base.activity.DynamicActivityImpl;
import dunglt.temporal.base.activity.IInboxActivity;
import dunglt.temporal.base.activity.INotificationActivity;
import dunglt.temporal.base.model.MActivity;
import dunglt.temporal.base.model.MInbox;
import dunglt.temporal.base.model.MWorkflow;
import dunglt.temporal.base.utility.Converter;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.common.converter.EncodedValues;
import io.temporal.workflow.ActivityStub;
import io.temporal.workflow.DynamicWorkflow;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DynamicWorkflowImpl implements DynamicWorkflow {
    private static final Logger logger = LoggerFactory.getLogger(DynamicWorkflowImpl.class);

    private IInboxActivity inboxActivity;
    private INotificationActivity notificationActivity;
    private int currentActivityIndex = 0;
    private Map<String, String> activityRequest = new HashMap<>();

    @Override
    public Object execute(EncodedValues args) {
        MWorkflow mWorkflow = args.get(0, MWorkflow.class);
        List<MActivity> activityList = Converter.convertFromObjToActivityList(args.get(1, List.class));
        Object currentSendData = args.get(2, Object.class);
        String currentRequestId =  args.get(3, String.class);

        this.inboxActivity = Workflow.newActivityStub(IInboxActivity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(30))
                        .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(5).build()).build());
        this.notificationActivity = Workflow.newActivityStub(INotificationActivity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofMinutes(2))
                        .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(5).build()).build());

        try{
            for (MActivity mActivity : activityList){
                Map<String, Object> activityData;
                ActivityStub activity = Workflow.newUntypedActivityStub(getActivityOptions(mActivity));

                if (!mActivity.getSequenceNo().equals(0)){
                    currentRequestId = "request-" + mActivity.getSequenceNo();
                }

                activityRequest.put(mActivity.getActivityType(), currentRequestId);

                //begin saga pattern
                inboxActivity.createNewInbox(mActivity, currentRequestId);

                inboxActivity.updateInbox(MInbox.STATUS_PROCESSING, currentRequestId, currentSendData, null);

                if (mActivity.getSequenceNo().equals(0)){
                    continue; // Skip activities with sequence 0
                }

                this.currentActivityIndex++; // index tracking for compensation step

                // Execute activity and handle compensation if it fails
                try{
                    activityData =  activity.execute("DynamicActivityImpl", Map.class, mActivity);
                    currentSendData = activityData.get("responseData");
                }catch (Exception e){
                    logger.info("Error in activity {}, starting compensation step", mActivity.getActivityType(), e);
                    processCompensationStep(activityList, e);
                    return null; // End workflow execution after compensation
                }

                inboxActivity.updateInbox(MInbox.STATUS_COMPLETED, currentRequestId, null, currentSendData);

                // End saga pattern
            }

            // After all activities are completed successfully, send notification
            processNotificationStep(activityList);

        }catch (Exception e) {
            logger.error("Error executing workflow: {}", mWorkflow.getWorkflowType(), e);
        }

        return null;
    }

    private ActivityOptions getActivityOptions(MActivity mActivity){
        int retryAttempts = 3;
        if (mActivity.getRetryAttempt() != null && mActivity.getRetryAttempt() > 0){
            retryAttempts = mActivity.getRetryAttempt();
        }

        return ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofSeconds(30))
                .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(retryAttempts).build())
                .build();
    }

    private void processCompensationStep(List<MActivity> activityList, Exception e){
        for (int i = currentActivityIndex; i >= 0; i--){
            String requestId = activityRequest.get(activityList.get(i).getActivityType());
            notificationActivity.sendNotification("Compensation for activity: "
                    + activityList.get(i).getActivityType());
            if (activityList.get(i).getSequenceNo().equals(currentActivityIndex)){
                // Update inbox with failure status and error message for the failed activity
                inboxActivity.updateInbox(MInbox.STATUS_FAILED, requestId, null, e.toString());
                continue;
            }

            inboxActivity.updateInbox(MInbox.STATUS_FAILED, requestId, null, null);
        }

        logger.info("Compensation completed for workflow");
    }

    private void processNotificationStep(List<MActivity> activityList){
        for (MActivity mActivity : activityList){
            if (StringUtils.hasText(mActivity.getNotifyMethod())){
                String requestId = activityRequest.get(mActivity.getActivityType());
                notificationActivity.sendNotification("Completed activity: " + mActivity.getActivityType());
                inboxActivity.updateInbox(MInbox.STATUS_NOTIFIED, requestId, null, null);
            }
        }
        logger.info("Notification completed for workflow");
    }
}
