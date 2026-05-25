package dunglt.temporal.base.activity;

public class IInboxActivityImpl implements IInboxActivity {
    @Override
    public String createNewInbox(String data) {
        System.out.println("Create new inbox with data: " + data);

        return "";
    }

    @Override
    public String updateInbox(String typeUpdate, String activity) {
        return "";
    }

   \
}
