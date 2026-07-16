package dunglt.temporal.base.service;

import dunglt.temporal.api.dto.DataDTO;
import dunglt.temporal.base.model.MActivity;
import dunglt.temporal.base.model.MWorkflow;
import dunglt.temporal.base.repository.WorkflowRepository;
import dunglt.temporal.error.service.ErrorService;
import io.temporal.api.enums.v1.WorkflowIdConflictPolicy;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class WorkflowClientService {

    private final WorkflowClient workflowClient;
    private final WorkflowRepository workflowRepository;
    private final ActivityService activityService;
    private final ErrorService errorService;

    public WorkflowClientService(WorkflowClient workflowClient, WorkflowRepository workflowRepository, ActivityService activityService, ErrorService errorService) {
        this.workflowClient = workflowClient;
        this.workflowRepository = workflowRepository;
        this.activityService = activityService;
        this.errorService = errorService;
    }

    public void startWorkflow(DataDTO sendData, String requestId) {
        String workflowType = sendData.getWorkflowType();
        MWorkflow mWorkflow = workflowRepository.findByWorkflowType(workflowType);

        WorkflowOptions options = getWorkflowOptions(mWorkflow, requestId);
        WorkflowStub stub = workflowClient.newUntypedWorkflowStub(
                "DynamicWorkflowImpl",
                options
        );

        List<MActivity> activityList = activityService.getListActivityByWorkflowId(mWorkflow.getWorkflowId());

        stub.start(mWorkflow, activityList, sendData, requestId);

    }

    private WorkflowOptions getWorkflowOptions(MWorkflow mWorkflow, String requestId) {
        return WorkflowOptions.newBuilder()
                .setTaskQueue(mWorkflow.getWorkflowTaskqueue())
                .setWorkflowId(requestId)
                .build();
    }
}
