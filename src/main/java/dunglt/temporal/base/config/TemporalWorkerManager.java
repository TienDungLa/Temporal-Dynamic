package dunglt.temporal.base.config;

import dunglt.temporal.base.activity.DynamicActivityImpl;
import dunglt.temporal.base.activity.IInboxActivityImpl;
import dunglt.temporal.base.activity.NotificationActivityImpl;

import dunglt.temporal.base.service.ActivityService;
import dunglt.temporal.base.service.WorkflowService;
import dunglt.temporal.base.utility.TriggerRegistry;
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
    private final WorkflowClient workflowClient;
    private  TriggerRegistry triggerRegistry;
    private Map<String, Worker> activeWorker = new HashMap<>();


    public TemporalWorkerManager(WorkflowService workflowService, WorkflowClient workflowClient, TriggerRegistry triggerRegistry) {
        this.workflowService = workflowService;
        this.workflowClient = workflowClient;
        this.triggerRegistry = triggerRegistry;
    }

    @PostConstruct
    private void firstInitBean() throws Exception {
        initWorkerFactory();
        getActiveWorkersFromFactory();
        workerFactory.start();
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

    public String reloadWorkerFactory(){

        if (workerFactory == null) {
            return "Worker factory is not initialized yet.";
        }

        workerFactory.shutdown();
        initWorkerFactory();
        getActiveWorkersFromFactory();
        triggerRegistry.reloadConfig();
        workerFactory.start();
        return "Reloaded workers successfully. ";
    }

}
