package dunglt.temporal.base.activity;

import dunglt.temporal.api.dto.DataDTO;
import dunglt.temporal.base.model.MActivity;import dunglt.temporal.base.service.InboxService;
import dunglt.temporal.base.utility.SpringContextBridge;
import dunglt.temporal.base.utility.TemporalConstant;
import dunglt.temporal.error.service.ErrorService;
import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;

public class IInboxActivityImpl implements IInboxActivity {
    private InboxService inboxService;
    private ErrorService errorService;


    @Override
    public String createNewInbox(MActivity mActivity, String requestId) {
        ActivityExecutionContext ctx = Activity.getExecutionContext();
        errorService = SpringContextBridge.getBean(ErrorService.class);

        try{
            inboxService = SpringContextBridge.getBean(InboxService.class);
            inboxService.createInbox(mActivity, requestId, ctx.getInfo().getWorkflowId());
        }catch (Exception e){
            errorService.nonRetryableError("Exception: " + e.getMessage() + " when create inbox for requestId: " + requestId);
        }

        return "";
    }

    @Override
    public String updateInbox(String typeUpdate, String requestId, Object data) {
        inboxService = SpringContextBridge.getBean(InboxService.class);
        errorService = SpringContextBridge.getBean(ErrorService.class);

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
            errorService.nonRetryableError("Exception: " + e.getMessage() + " when update inbox with requestId: " + requestId);
        }


        return "";
    }


}
