package dunglt.temporal.base.activity;

import dunglt.temporal.api.dto.DataDTO;
import dunglt.temporal.base.model.MActivity;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface IInboxActivity {

    @ActivityMethod
    String createNewInbox(MActivity mActivity, String requestId);

    @ActivityMethod
    String updateInbox(String typeUpdate, String activity, Object data, Object response);

}
