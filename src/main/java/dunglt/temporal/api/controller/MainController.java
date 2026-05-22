package dunglt.temporal.api.controller;

import dunglt.temporal.base.config.TemporalWorkerManager;
import dunglt.temporal.base.model.MActivity;
import dunglt.temporal.base.model.MWorkflow;
import dunglt.temporal.base.service.ActivityService;
import dunglt.temporal.base.service.WorkflowService;
import io.temporal.worker.Worker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/main")
public class MainController {

    private final WorkflowService workflowService;
    private final ActivityService activityService;
    private  TemporalWorkerManager temporalWorkerManager;

    public MainController(WorkflowService workflowService, ActivityService activityService, TemporalWorkerManager temporalWorkerManager) {
        this.workflowService = workflowService;
        this.activityService = activityService;
        this.temporalWorkerManager = temporalWorkerManager;
    }

    @GetMapping("/active-workers")
    public List<String> getAllWorkers() {
        Map<String, Worker> activeWorkers = temporalWorkerManager.getActiveWorkersFromFactory();
        return activeWorkers.keySet().stream().toList();
    }

    @GetMapping("/register")
    public ResponseEntity<String> registerWorkflow(MWorkflow mWorkflow) {
        workflowService.createConfigWorkflow(mWorkflow);
        return new ResponseEntity<>("Workflow registered successfully", HttpStatus.OK);
    }

    @PostMapping("/manual-reload")
    public String reloadWorkerFactory(){
        return temporalWorkerManager.reloadWorkerFactory();
    }

    @PostMapping("/register-activity")
    public String createActivity(MActivity mActivity){
        return activityService.createConfigActivity(mActivity);
    }

}

