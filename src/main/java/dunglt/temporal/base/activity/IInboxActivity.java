package dunglt.temporal.base.activity;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface IInboxActivity {

    String createNewInbox(String data);

    String updateInbox(String data);

}
