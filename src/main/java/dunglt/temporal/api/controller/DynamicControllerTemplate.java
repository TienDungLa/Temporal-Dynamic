package dunglt.temporal.api.controller;

import dunglt.temporal.api.dto.DataDTO;
import dunglt.temporal.base.service.WorkflowClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class DynamicControllerTemplate {

    @Autowired
    protected WorkflowClientService workflowClientService;

    // Common logic cho tất cả dynamic controllers
    public String executeWorkflow(@RequestBody  @Valid DataDTO data,
                                     @RequestHeader (name = "Request-ID") String requestId) {

        workflowClientService.startWorkflow(data, requestId);
        return "Workflow started successfully";
    }

}

