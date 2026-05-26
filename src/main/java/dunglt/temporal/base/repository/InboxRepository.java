package dunglt.temporal.base.repository;

import dunglt.temporal.base.model.MInbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InboxRepository extends JpaRepository<MInbox, Integer> {
    MInbox findByRequestId(String requestId);
}
