package dunglt.temporal.base.service;

import dunglt.temporal.base.model.MWorkflow;
import dunglt.temporal.base.repository.InboxRepository;
import dunglt.temporal.base.repository.WorkflowRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final InboxRepository inboxRepository;


    public WorkflowService(WorkflowRepository workflowRepository, InboxRepository inboxRepository) {
        this.workflowRepository = workflowRepository;
        this.inboxRepository = inboxRepository;
    }

    public List<String> getListWorkflowTaskQueue() {
        return workflowRepository.findAllWorkflowTaskQueue();
    }

    public void createConfigWorkflow(MWorkflow mWorkflow) {
        workflowRepository.save(mWorkflow);
    }

    public List<MWorkflow> getAllWorkflow() {
        return workflowRepository.findAll();
    }

    public MWorkflow findByWorkflowType(String workflowType) {
        return workflowRepository.findByWorkflowType(workflowType);
    }

    public MWorkflow findByWorkflowId(Integer workflowId) {
        return workflowRepository.findByWorkflowId(workflowId);
    }

    public boolean isWorkflowExist(String workflowId) {
        return inboxRepository.existsByWorkflowId(workflowId) > 0;
    }
}
