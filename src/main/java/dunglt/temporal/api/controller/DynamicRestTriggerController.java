package dunglt.temporal.api.controller;

import dunglt.temporal.api.dto.DataDTO;
import dunglt.temporal.base.service.WorkflowTriggerService;
import dunglt.temporal.base.utility.TemporalConstant;
import dunglt.temporal.base.utility.TriggerRegistry;
import dunglt.temporal.error.service.ErrorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DynamicRestTriggerController {
    private final ErrorService errorService;
    private final WorkflowTriggerService workflowTriggerService;

    public DynamicRestTriggerController(ErrorService errorService, WorkflowTriggerService workflowTriggerService) {
        this.errorService = errorService;
        this.workflowTriggerService = workflowTriggerService;
    }

    @RequestMapping("/**")
    public ResponseEntity<?> trigger(HttpServletRequest request,
                                     @RequestBody(required = true) DataDTO body,
                                     @RequestHeader(name = "Request-ID", required = true) String requestId) {
        String path = request.getRequestURI();

        return ResponseEntity.accepted().body(
                workflowTriggerService.validateAndStartWorkflow("Rest",requestId, body, path)
        );
    }
}
