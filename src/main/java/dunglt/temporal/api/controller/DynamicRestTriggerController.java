package dunglt.temporal.api.controller;

import dunglt.temporal.api.dto.DataDTO;
import dunglt.temporal.base.service.WorkflowTriggerService;
import dunglt.temporal.error.service.ErrorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DynamicRestTriggerController {
    private final ErrorService errorService;
    private final WorkflowTriggerService workflowTriggerService;

    public DynamicRestTriggerController(ErrorService errorService, WorkflowTriggerService workflowTriggerService) {
        this.errorService = errorService;
        this.workflowTriggerService = workflowTriggerService;
    }

    @PostMapping("/**")
    @Valid
    public ResponseEntity<?> trigger(HttpServletRequest request,
                                     @RequestBody(required = true) DataDTO body,
                                     @RequestHeader(name = "Request-ID", required = true) String requestId) {
        String url = request.getRequestURL().toString();

        return ResponseEntity.accepted().body(
                workflowTriggerService.validateAndStartWorkflow("Rest",requestId, body, url)
        );
    }
}
