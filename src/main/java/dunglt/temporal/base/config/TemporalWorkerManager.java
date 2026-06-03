package dunglt.temporal.base.config;

import dunglt.temporal.base.activity.DynamicActivityImpl;
import dunglt.temporal.base.activity.IInboxActivityImpl;
import dunglt.temporal.base.activity.NotificationActivityImpl;
import dunglt.temporal.base.model.MActivity;
import dunglt.temporal.base.model.MWorkflow;
import dunglt.temporal.base.service.ActivityService;
import dunglt.temporal.base.service.WorkflowService;
import dunglt.temporal.base.utility.DynamicControllerGenerator;
import dunglt.temporal.base.workflow.DynamicWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TemporalWorkerManager {
    private WorkerFactory workerFactory;
    private final WorkflowService workflowService;
    private final ActivityService activityService;
    private final WorkflowClient workflowClient;
    private final DynamicControllerGenerator dynamicControllerGenerator;
    private Map<String, Worker> activeWorker = new HashMap<>();


    public TemporalWorkerManager(WorkflowService workflowService, ActivityService activityService, WorkflowClient workflowClient, DynamicControllerGenerator dynamicControllerGenerator) {
        this.workflowService = workflowService;
        this.activityService = activityService;
        this.workflowClient = workflowClient;
        this.dynamicControllerGenerator = dynamicControllerGenerator;
    }

    @PostConstruct
    private void firstInitBean() throws Exception {
        initWorkerFactory();
        getActiveWorkersFromFactory();
        workerFactory.start();
        loadControllerClass();
    }

    public Map<String, Worker> getActiveWorkersFromFactory() {
        activeWorker.clear();
        for (String tq : workflowService.getListWorkflowTaskQueue()){
            activeWorker.put(tq, workerFactory.getWorker(tq));
        };
        return activeWorker;
    }

    private void initWorkerFactory(){
        this.workerFactory = WorkerFactory.newInstance(workflowClient);
        List<String> taskQueues = workflowService.getListWorkflowTaskQueue();

        for (String taskQueue : taskQueues) {
            Worker worker = workerFactory.newWorker(taskQueue);
            worker.registerWorkflowImplementationTypes(DynamicWorkflowImpl.class);
            worker.registerActivitiesImplementations(new IInboxActivityImpl());
            worker.registerActivitiesImplementations(new DynamicActivityImpl());
            worker.registerActivitiesImplementations(new NotificationActivityImpl());
        }
    }

    private void loadControllerClass() {
        List<MActivity> activityList = activityService.getAllActivity0();
        for (MActivity mActivity : activityList){
            dynamicControllerGenerator.generateAndRegister(mActivity);
        }
    }

    public String reloadWorkerFactory(){
        workerFactory.shutdown();
        initWorkerFactory();
        getActiveWorkersFromFactory();
        workerFactory.start();
        dynamicControllerGenerator.unregisterAllGeneratedControllers();
        loadControllerClass();
        return "Reloaded workers successfully. ";
    }

}
