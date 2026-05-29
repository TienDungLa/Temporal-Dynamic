package dunglt.temporal.base.activity;

import dunglt.temporal.api.dto.DataDTO;
import dunglt.temporal.base.model.MActivity;import dunglt.temporal.base.service.InboxService;
import dunglt.temporal.base.utility.SpringContextBridge;
import dunglt.temporal.base.utility.TemporalConstant;
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
    public String updateInbox(String typeUpdate, String requestId, Object data) {
        inboxService = SpringContextBridge.getBean(InboxService.class);
        System.out.println("Update inbox with data: " + typeUpdate + " - " + requestId);

        try{
            if (typeUpdate.equals(TemporalConstant.INBOX_STATUS_PROCESSING)){
                 inboxService.updateInbox(TemporalConstant.INBOX_STATUS_PROCESSING, requestId, data);
            } else if (typeUpdate.equals(TemporalConstant.INBOX_STATUS_COMPLETED)) {
                inboxService.updateInbox(TemporalConstant.INBOX_STATUS_COMPLETED, requestId, data);
            } else if (typeUpdate.equals(TemporalConstant.INBOX_STATUS_NOTIFIED)) {
                inboxService.updateInbox(TemporalConstant.INBOX_STATUS_NOTIFIED, requestId, data);
            } else if (typeUpdate.equals(TemporalConstant.INBOX_STATUS_FAILED)) {
                inboxService.updateInbox(TemporalConstant.INBOX_STATUS_FAILED, requestId, data);
            } else if (typeUpdate.equals(TemporalConstant.INBOX_STATUS_COMPENSATION)) {
                inboxService.updateInbox(TemporalConstant.INBOX_STATUS_COMPENSATION, requestId, null);
            }
        }catch (Exception e){

        }


        return "";
    }


}
