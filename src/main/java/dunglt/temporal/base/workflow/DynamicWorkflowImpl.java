package dunglt.temporal.base.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import dunglt.temporal.base.activity.IInboxActivity;
import dunglt.temporal.base.activity.INotificationActivity;
import dunglt.temporal.base.model.MActivity;
import dunglt.temporal.base.model.MWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.common.converter.EncodedValues;
import io.temporal.workflow.ActivityStub;
import io.temporal.workflow.DynamicWorkflow;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class DynamicWorkflowImpl implements DynamicWorkflow {
    private boolean isSagaPartern = false;
    private IInboxActivity inboxActivity;
    private INotificationActivity notificationActivity;
    private int currentActivityIndex = 0;
    private int totalActivities = 0;

    @Override
    public Object execute(EncodedValues args) {
        MWorkflow mWorkflow = args.get(0, MWorkflow.class);
        List<?> rawList = args.get(1, List.class);
        List<MActivity> activityList = convertToActivityList(rawList);

        this.isSagaPartern = mWorkflow.isSagaPattern();
        this.inboxActivity = Workflow.newActivityStub(IInboxActivity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(30))
                        .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(5).build()).build());
        this.notificationActivity = Workflow.newActivityStub(INotificationActivity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofMinutes(2))
                        .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(5).build()).build());

        String result = null;
        if (isSagaPartern){
            result  = SagaPatternExecute(mWorkflow, activityList);
        } else {

        }


        return null;
    }

    private String SagaPatternExecute(MWorkflow mWorkflow, List<MActivity> activityList){
        for (MActivity mActivity : activityList){
            ActivityStub activity = Workflow.newUntypedActivityStub(getActivityOptions(mActivity));

            //begin saga pattern

            inboxActivity.createNewInbox("create inbox for request");

            inboxActivity.updateInbox("Processing ");

            activity.execute("DynamicActivityImpl", Object.class, mActivity);

            inboxActivity.updateInbox("processed");

            notificationActivity.sendNotification("Notification for activity " + mActivity.getSequenceNo());
        }
        return null;
    }

    private ActivityOptions getActivityOptions(MActivity mActivity){
        return ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofSeconds(30))
                .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(5).build())
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
}
