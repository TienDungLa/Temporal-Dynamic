package dunglt.temporal.base.service;

import dunglt.temporal.api.dto.DataDTO;
import dunglt.temporal.base.model.MActivity;
import dunglt.temporal.base.model.MInbox;
import dunglt.temporal.base.repository.InboxRepository;
import dunglt.temporal.base.utility.Converter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class InboxService {

    private final InboxRepository inboxRepository;

    public InboxService(InboxRepository inboxRepository) {
        this.inboxRepository = inboxRepository;
    }

    public void createInbox(MActivity mActivity, String requestId, String workflowId){
        // Create new inbox in database with status "pending"
        MInbox mInbox = new MInbox();
        mInbox.setStatus(MInbox.STATUS_CREATED);
        mInbox.setWorkflowId(workflowId);
        mInbox.setRequestId(requestId);
        mInbox.setActivitySequence(mActivity.getSequenceNo());
        inboxRepository.save(mInbox);
    }

    public String updateInbox(String typeUpdate, String requestId, Object data){
        MInbox mInbox = inboxRepository.findByRequestId(requestId);

        if (typeUpdate.equals(MInbox.STATUS_PROCESSING)){
            mInbox.setStatus(MInbox.STATUS_PROCESSING);
            mInbox.setSendPayload(Converter.convertFromObjToJsonString(data));
            inboxRepository.save(mInbox);
            return "Processing";
        }

        if (typeUpdate.equals(MInbox.STATUS_COMPLETED)){
            mInbox.setStatus(MInbox.STATUS_COMPLETED);
            mInbox.setResponsePayload(Converter.convertFromObjToJsonString(data));
            inboxRepository.save(mInbox);
            return "Completed";
        }

        if (typeUpdate.equals(MInbox.STATUS_NOTIFIED)){
            mInbox.setStatus(MInbox.STATUS_NOTIFIED);
            inboxRepository.save(mInbox);
            return "Notified";
        }

        if (typeUpdate.equals(MInbox.STATUS_FAILED)){
            mInbox.setStatus(MInbox.STATUS_FAILED);
            if (data != null && StringUtils.hasText(data.toString())){
                mInbox.setErrorMessage(Converter.convertFromObjToJsonString(data));
            }
            inboxRepository.save(mInbox);
            return "Failed";
        }

        return "";
    }

}
