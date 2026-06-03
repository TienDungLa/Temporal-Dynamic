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
import dunglt.temporal.base.utility.SpringContextBridge;
import dunglt.temporal.base.utility.TemporalConstant;
import dunglt.temporal.error.service.ErrorService;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.common.converter.EncodedValues;
import io.temporal.failure.ActivityFailure;
import io.temporal.failure.ApplicationFailure;
import io.temporal.workflow.ActivityStub;
import io.temporal.workflow.DynamicWorkflow;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;


public class DynamicWorkflowImpl implements DynamicWorkflow {
    private static final Logger logger = LoggerFactory.getLogger(DynamicWorkflowImpl.class);

    private IInboxActivity inboxActivity;
    private INotificationActivity notificationActivity;
    private int currentActivityIndex = 0;
    private Map<String, String> activityRequest = new HashMap<>();
    private ErrorService errorService = SpringContextBridge.getBean(ErrorService.class);

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
                    currentRequestId = UUID.randomUUID().toString();
                }

                activityRequest.put(mActivity.getActivityType(), currentRequestId);

                //begin saga pattern
                inboxActivity.createNewInbox(mActivity, currentRequestId);

                inboxActivity.updateInbox(TemporalConstant.INBOX_STATUS_PROCESSING, currentRequestId, currentSendData);

                if (mActivity.getSequenceNo().equals(0)){
                    continue; // Skip activities with sequence 0
                }

                this.currentActivityIndex++; // index tracking for compensation step

                // Execute activity and handle compensation if it fails
                try{
                    activityData =  activity.execute("DynamicActivityImpl", Map.class, mActivity, currentSendData);
                    currentSendData = activityData.get("responseData");
                }catch (ActivityFailure e){
                    logger.warn("Error in activity {}, starting compensation step", mActivity.getActivityType());
                    processCompensationStep(activityList, e);
                    throw e; // End workflow execution after compensation
                }

                inboxActivity.updateInbox(TemporalConstant.INBOX_STATUS_COMPLETED, currentRequestId, currentSendData);

                // End saga pattern
            }

            // After all activities are completed successfully, send notification
            processNotificationStep(activityList);

        }catch (Exception e) {
            logger.info("Error executing workflow: {}", mWorkflow.getWorkflowType());
        }

        return null;
    }

    private ActivityOptions getActivityOptions(MActivity mActivity) {
        RetryOptions retryOptions = RetryOptions.newBuilder()
                .setMaximumAttempts(1)
                .build(); // default

        if (mActivity.getRetryAttempt() != null && mActivity.getRetryAttempt() > 0) {
            retryOptions = RetryOptions.newBuilder()
                    .setMaximumAttempts(mActivity.getRetryAttempt())
                    .build();
        }

        return ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofSeconds(30))
                .setRetryOptions(retryOptions)
                .build();
    }

    private void processCompensationStep(List<MActivity> activityList, Exception e){
        for (int i = currentActivityIndex; i >= 0; i--){
            String requestId = activityRequest.get(activityList.get(i).getActivityType());
            String notifyResult = null;

            // Update inbox with failure status and error message for the failed activity
            if (activityList.get(i).getSequenceNo().equals(currentActivityIndex)){
                inboxActivity.updateInbox(TemporalConstant.INBOX_STATUS_FAILED, requestId, e.getCause().toString());
            }else{
                inboxActivity.updateInbox(TemporalConstant.INBOX_STATUS_FAILED, requestId,null);
            }

            notifyResult = notificationActivity.sendNotification(TemporalConstant.NOTIFY_TYPE_COMPENSATION, activityList.get(i));

            inboxActivity.updateInbox(TemporalConstant.INBOX_STATUS_COMPENSATION, requestId, notifyResult);
        }

        logger.info("Compensation completed for workflow");
    }

    private void processNotificationStep(List<MActivity> activityList){
        for (MActivity mActivity : activityList){
            if (StringUtils.hasText(mActivity.getNotifyMethod())){
                String requestId = activityRequest.get(mActivity.getActivityType());
                String notifyResult = null;

                notifyResult = notificationActivity.sendNotification(TemporalConstant.NOTIFY_TYPE_COMPLETED, mActivity);
                inboxActivity.updateInbox(TemporalConstant.INBOX_STATUS_NOTIFIED, requestId, notifyResult);
            }
        }
        logger.info("Notification completed for workflow");
    }
}
