package dunglt.temporal.base.activity;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface INotificationActivity {
    String sendNotification(String message);
}
