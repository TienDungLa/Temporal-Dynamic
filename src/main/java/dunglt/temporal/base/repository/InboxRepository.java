package dunglt.temporal.base.repository;

import dunglt.temporal.base.model.MInbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InboxRepository extends JpaRepository<MInbox, Integer> {

    @Query("SELECT COUNT(*) FROM MInbox i WHERE i.workflowId = :workflowId")
    Integer existsByWorkflowId(String workflowId);

    MInbox findByRequestId(String requestId);
}
