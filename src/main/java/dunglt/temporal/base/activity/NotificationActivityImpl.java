package dunglt.temporal.base.activity;

public class NotificationActivityImpl implements INotificationActivity{
    @Override
    public String sendNotification(String message) {
        System.out.println("Notification sent: " + message);
        return "";
    }
}
