package dunglt.temporal.base.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import dunglt.temporal.base.activity.DynamicActivityImpl;
import dunglt.temporal.base.activity.IInboxActivity;
import dunglt.temporal.base.activity.INotificationActivity;
import dunglt.temporal.base.model.MActivity;
import dunglt.temporal.base.model.MInbox;
import dunglt.temporal.base.model.MWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.common.converter.EncodedValues;
import io.temporal.workflow.ActivityStub;
import io.temporal.workflow.DynamicWorkflow;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class DynamicWorkflowImpl implements DynamicWorkflow {
    private static final Logger logger = LoggerFactory.getLogger(DynamicWorkflowImpl.class);

    private IInboxActivity inboxActivity;
    private INotificationActivity notificationActivity;
    private int currentActivityIndex = 0;

    @Override
    public Object execute(EncodedValues args) {
        MWorkflow mWorkflow = args.get(0, MWorkflow.class);
        List<?> rawList = args.get(1, List.class);
        List<MActivity> activityList = convertToActivityList(rawList);

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
                ActivityStub activity = Workflow.newUntypedActivityStub(getActivityOptions(mActivity));
                this.currentActivityIndex++;

                //begin saga pattern
                inboxActivity.createNewInbox(mActivity.getActivityType());

                inboxActivity.updateInbox(MInbox.STATUS_PROCESSING, mActivity.getActivityType());

                if (mActivity.getSequenceNo().equals(0)){
                    continue; // Skip activities with sequence 0
                }

                try{
                    Object response =  activity.execute("DynamicActivityImpl", Object.class, mActivity);

                }catch (Exception e){
                    logger.info("Error in activity {}, starting compensation step", mActivity.getActivityType(), e);
                    processCompensationStep(activityList);
                    return null; // End workflow execution after compensation
                }

                inboxActivity.updateInbox(MInbox.STATUS_COMPLETED, mActivity.getActivityType());
            }

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

    private List<MActivity> convertToActivityList(List<?> rawList) {
        List<MActivity> activityList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for (Object item : rawList) {
            if (item instanceof MActivity) {
                activityList.add((MActivity) item);
            } else {
                MActivity activity = mapper.convertValue(item, MActivity.class);
                activityList.add(activity);
            }
        }
        return activityList;
    }

    private void processCompensationStep(List<MActivity> activityList){
        for (int i = currentActivityIndex; i >= 0; i--){
            notificationActivity.sendNotification("Compensation for activity: "
                    + activityList.get(i).getActivityType());
            inboxActivity.updateInbox(MInbox.STATUS_FAILED, activityList.get(i).getActivityType());
        }

        logger.info("Compensation completed for workflow");
    }

    private void processNotificationStep(List<MActivity> activityList){
        for (MActivity mActivity : activityList){
            notificationActivity.sendNotification("Completed activity: " + mActivity.getActivityType());
            inboxActivity.updateInbox(MInbox.STATUS_NOTIFIED, mActivity.getActivityType());
        }
        logger.info("Notification completed for workflow");
    }
}
