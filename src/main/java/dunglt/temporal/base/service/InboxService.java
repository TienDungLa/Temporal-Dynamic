package dunglt.temporal.base.service;

import dunglt.temporal.base.repository.InboxRepository;
import org.springframework.stereotype.Service;

@Service
public class InboxService {

    private final InboxRepository inboxRepository;

    public InboxService(InboxRepository inboxRepository) {
        this.inboxRepository = inboxRepository;
    }


}
