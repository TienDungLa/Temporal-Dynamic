package dunglt.temporal.base.repository;

import dunglt.temporal.base.model.MActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<MActivity, Integer> {

    MActivity findByWorkflowIdAndSequenceNo(Integer workflowId, Integer sequence);

    @Query("SELECT a FROM MActivity a WHERE a.workflowId = :workflowId ORDER BY a.sequenceNo ASC")
    List<MActivity> findByWorkflowId(Integer workflowId);
}
