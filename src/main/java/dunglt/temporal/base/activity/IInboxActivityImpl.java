package dunglt.temporal.base.activity;

import dunglt.temporal.api.dto.DataDTO;
import dunglt.temporal.base.model.MActivity;
import dunglt.temporal.base.model.MInbox;
import dunglt.temporal.base.service.InboxService;
import dunglt.temporal.base.utility.SpringContextBridge;
import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;

public class IInboxActivityImpl implements IInboxActivity {
    private InboxService inboxService;


    @Override
    public String createNewInbox(MActivity mActivity, String requestId) {
        ActivityExecutionContext ctx = Activity.getExecutionContext();

        inboxService = SpringContextBridge.getBean(InboxService.class);
        System.out.println("Create new inbox with data: " + mActivity.getActivityType());
        inboxService.createInbox(mActivity, requestId, ctx.getInfo().getWorkflowId());

        return "";
    }

    @Override
    public String updateInbox(String typeUpdate, String requestId, Object data, Object response) {
        inboxService = SpringContextBridge.getBean(InboxService.class);
        System.out.println("Update inbox with data: " + typeUpdate + " - " + requestId);

        try{
            if (typeUpdate.equals(MInbox.STATUS_PROCESSING)){
                 inboxService.updateInbox(MInbox.STATUS_PROCESSING, requestId, data);
            } else if (typeUpdate.equals(MInbox.STATUS_COMPLETED)) {
                inboxService.updateInbox(MInbox.STATUS_COMPLETED, requestId, response);
            } else if (typeUpdate.equals(MInbox.STATUS_NOTIFIED)) {
                inboxService.updateInbox(MInbox.STATUS_NOTIFIED, requestId, null);
            } else if (typeUpdate.equals(MInbox.STATUS_FAILED)) {
                inboxService.updateInbox(MInbox.STATUS_FAILED, requestId, response);
            }
        }catch (Exception e){

        }


        return "";
    }


}
