package dunglt.temporal.base.utility;

import dunglt.temporal.base.model.*;
import dunglt.temporal.base.service.ActivityService;
import dunglt.temporal.base.service.ConnectionService;
import dunglt.temporal.base.service.WorkflowService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class TriggerRegistry {
    private static final Logger logger = LoggerFactory.getLogger(TriggerRegistry.class);

    private final AtomicReference<List<TriggerDefinition>> triggers = new AtomicReference<>(List.of());
    private final ActivityService activityService;
    private final WorkflowService workflowService;
    private final ConnectionService connectionService;

    public TriggerRegistry(ActivityService activityService, WorkflowService workflowService, ConnectionService connectionService) {
        this.activityService = activityService;
        this.workflowService = workflowService;
        this.connectionService = connectionService;
    }

    @PostConstruct
    private void postConstruct() {
        reloadConfig();
    }

    public void reloadConfig() {
        List<MActivity> activities = activityService.getAllActivity0();
        List<TriggerDefinition> newTriggers = new ArrayList<>();

        for (MActivity activity : activities) {
            MWorkflow workflow = workflowService.findByWorkflowId(activity.getWorkflowId());
            if (workflow == null || workflow.getWorkflowType() == null) {
                logger.warn("Skip trigger registration because workflow not found for activityId={}", activity.getActivityId());
                continue;
            }

            String workflowType = workflow.getWorkflowType();
            String sendMethod = activity.getSendMethod();

            TriggerDefinition triggerInfo = null;
            for (TriggerDefinition t : newTriggers) {
                if (workflowType.equals(t.getWorkflowType())) {
                    triggerInfo = t;
                    break;
                }
            }
            if (triggerInfo == null) {
                triggerInfo = new TriggerDefinition();
                triggerInfo.setWorkflowType(workflowType);
                newTriggers.add(triggerInfo);
            }

            if (TemporalConstant.SEND_METHOD_REST.equals(sendMethod)) {
                MRestConfig config = connectionService
                        .getRestConfigByActivityIdAndType(activity.getActivityId(), TemporalConstant.REST_CONFIG_TYPE_SEND);

                if (config != null) {
                    triggerInfo.setPath(config.getUrl());
                    triggerInfo.setSendMethod(TemporalConstant.SEND_METHOD_REST);
                    logger.info("Register trigger for workflow: {}, activity: {}, send method: {}, url: {}",
                            workflowType, activity.getActivityType(), sendMethod, config.getUrl());
                } else {
                    logger.warn("REST config not found for activityId={}", activity.getActivityId());
                }
            } else if (TemporalConstant.SEND_METHOD_MQ.equals(sendMethod)) {
                MKafkaConfig config = connectionService
                        .getKafkaConfigByActivityIdAndType(activity.getActivityId(), TemporalConstant.MQ_CONFIG_TYPE_SEND);

                if (config != null) {
                    triggerInfo.setTopic(config.getTopic());
                    triggerInfo.setSendMethod(TemporalConstant.SEND_METHOD_MQ);
                    logger.info("Register trigger for workflow: {}, activity: {}, send method: {}, topic: {}",
                            workflowType, activity.getActivityType(), sendMethod, config.getTopic());
                } else {
                    logger.warn("Kafka config not found for activityId={}", activity.getActivityId());
                }
            } else {
                logger.warn("Unsupported send method '{}' for activityId={}", sendMethod, activity.getActivityId());
            }
        }

        // Atomic swap to make reload visible in one step
        this.triggers.set(List.copyOf(newTriggers));
        logger.info("Trigger config reloaded. Total workflows cached: {}", newTriggers.size());
    }

    public Boolean validateTrigger(String workflowType, String sendMethod, String path) {
        TriggerDefinition triggerInfo = null;
        for (TriggerDefinition t : triggers.get()) {
            if (workflowType.equals(t.getWorkflowType())) {
                triggerInfo = t;
                break;
            }
        }
        if (triggerInfo == null) {
            logger.warn("No trigger found for workflow type: {}", workflowType);
            return false;
        }

        String triggerPath;
        if (TemporalConstant.SEND_METHOD_REST.equals(sendMethod)) {
            triggerPath = triggerInfo.getPath();
        } else if (TemporalConstant.SEND_METHOD_MQ.equals(sendMethod)) {
            triggerPath = triggerInfo.getTopic();
        } else {
            logger.warn("Unsupported send method '{}' for workflow type: {}", sendMethod, workflowType);
            return false;
        }

        if (triggerPath == null) {
            logger.warn("No trigger found for workflow type: {}, send method: {}", workflowType, sendMethod);
            return false;
        }

        return normalizeRestPath(triggerPath).equals(normalizeRestPath(path));
    }

    private String normalizeRestPath(String path) {
        if (path == null)
            return "";
        String p = path.trim();
        if (p.startsWith("/api/"))
            p = p.substring(4);
        if (!p.startsWith("/"))
            p = "/" + p;
        return p;
    }
}
