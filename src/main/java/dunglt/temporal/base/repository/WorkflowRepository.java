package dunglt.temporal.base.repository;

import dunglt.temporal.base.model.MWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowRepository extends JpaRepository<MWorkflow, Integer> {

    MWorkflow findByWorkflowType(String workflowType);

    @Query("SELECT w.workflowTaskqueue FROM MWorkflow w")
    List<String> findAllWorkflowTaskQueue();

    @Query("SELECT count(*) FROM MActivity ma WHERE ma.workflowId = :workflowId")
    Integer getActivityNumberByWorkflowId(Integer workflowId);
}

