package dunglt.temporal.base.service;

import dunglt.temporal.base.model.MActivity;
import dunglt.temporal.base.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {

        this.activityRepository = activityRepository;
    }

    public String createConfigActivity(MActivity mActivity){
        activityRepository.save(mActivity);
        return "Create activity successfully";
    }

    public List<MActivity> getListActivityByWorkflowId(Integer workflowId){
        return activityRepository.findByWorkflowId(workflowId);
    }

    public List<MActivity> getAllActivity0(){
        return activityRepository.getAllActivity0();
    }
}
