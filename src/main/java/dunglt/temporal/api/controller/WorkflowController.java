package dunglt.temporal.api.controller;


import dunglt.temporal.api.dto.DataDTO;
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
    public String StartWorkflow(DataDTO sendData) {
        workflowClientService.startWorkflow(sendData);
        return "Workflow started successfully";
    }

}
