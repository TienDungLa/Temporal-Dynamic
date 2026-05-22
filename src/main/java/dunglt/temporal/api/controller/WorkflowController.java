package dunglt.temporal.api.controller;


import dunglt.temporal.base.service.WorkflowClientService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {
    private WorkflowClientService workflowClientService;

    public WorkflowController(WorkflowClientService workflowClientService) {
        this.workflowClientService = workflowClientService;
    }


    @PostMapping("/start")
    public String StartWorkflow(String workflowType) {
        workflowClientService.startWorkflow(workflowType);
        return "Workflow started: " + workflowType;
    }

}
