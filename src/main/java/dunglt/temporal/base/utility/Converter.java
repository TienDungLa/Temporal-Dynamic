package dunglt.temporal.base.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import dunglt.temporal.api.dto.DataDTO;
import dunglt.temporal.base.model.MActivity;

import java.util.ArrayList;
import java.util.List;

public class Converter {

    public static List<MActivity> convertFromObjToActivityList(List<?> rawList) {
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

    public static String convertFromObjToJsonString(Object data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
