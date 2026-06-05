package dunglt.temporal.base.service;

import dunglt.temporal.api.dto.DataDTO;
import dunglt.temporal.base.model.MWorkflow;
import dunglt.temporal.base.utility.TemporalConstant;
import dunglt.temporal.base.utility.TriggerRegistry;
import dunglt.temporal.error.service.ErrorService;
import org.springframework.stereotype.Service;

/*
 * This service is responsible for validate the trigger conditions for workflow execution
 */
@Service
public class WorkflowTriggerService {
    private final WorkflowService workflowService;
    private final WorkflowClientService workflowClientService;
    private final ErrorService errorService;
    private TriggerRegistry triggerRegistry;

    public WorkflowTriggerService(WorkflowService workflowService, WorkflowClientService workflowClientService, ErrorService errorService, TriggerRegistry triggerRegistry) {
        this.workflowService = workflowService;
        this.workflowClientService = workflowClientService;
        this.errorService = errorService;
        this.triggerRegistry = triggerRegistry;
    }

    public String validateAndStartWorkflow(String sendMethod, String requestId, DataDTO sendData, String path) {
        String workflowType = sendData.getWorkflowType();
        MWorkflow mWorkflow = workflowService.findByWorkflowType(workflowType);

        if (mWorkflow ==null){
            throw errorService.notFound("WORKFLOW_TYPE_NOT_FOUND",
                    "Workflow type not found",
                    "workflowType=" + workflowType);
        }

        if (!triggerRegistry.validateTrigger(workflowType, TemporalConstant.SEND_METHOD_REST, path)){
            errorService.notFound("NOT_FOUND",
                    "No trigger found for workflow type: " + workflowType + " and path: " + path
                    , null);
        }

        workflowClientService.startWorkflow(sendData, requestId);
        return "Workflow started successfully";
    }
}
