package dunglt.temporal.base.activity;

import dunglt.temporal.base.model.MActivity;
import io.temporal.activity.DynamicActivity;
import io.temporal.common.converter.EncodedValues;

public class DynamicActivityImpl implements DynamicActivity {

    @Override
    public Object execute(EncodedValues args) {
        MActivity mActivity = args.get(0, MActivity.class);


        return null;
    }

}
