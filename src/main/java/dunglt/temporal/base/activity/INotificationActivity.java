package dunglt.temporal.base.activity;

import dunglt.temporal.base.model.MActivity;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface INotificationActivity {
    String sendNotification(String typeNotify, MActivity mActivity);
}
