package dunglt.temporal.base.service;

import dunglt.temporal.base.model.MActivity;
import dunglt.temporal.base.model.MWorkflow;
import dunglt.temporal.base.repository.WorkflowRepository;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkflowClientService {

    private final WorkflowClient workflowClient;
    private final WorkflowRepository workflowRepository;
    private final ActivityService activityService;

    public WorkflowClientService(WorkflowClient workflowClient, WorkflowRepository workflowRepository, ActivityService activityService) {
        this.workflowClient = workflowClient;
        this.workflowRepository = workflowRepository;
        this.activityService = activityService;
    }

    public void startWorkflow(String workflowType) {
        MWorkflow mWorkflow = workflowRepository.findByWorkflowType(workflowType);
        WorkflowOptions options = getWorkflowOptions(mWorkflow);

        WorkflowStub stub = workflowClient.newUntypedWorkflowStub(
                "DynamicWorkflowImpl",
                options
        );

        List<MActivity> activityList = activityService.getListActivityByWorkflowId(mWorkflow.getWorkflowId());

        stub.start(mWorkflow, activityList);
    }

    private WorkflowOptions getWorkflowOptions(MWorkflow mWorkflow) {
        return WorkflowOptions.newBuilder()
                .setTaskQueue(mWorkflow.getWorkflowTaskqueue())
                .setWorkflowId(mWorkflow.getWorkflowType() + " " + System.currentTimeMillis())
                .build();
    }
}
