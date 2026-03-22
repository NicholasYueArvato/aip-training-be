package de.arvato.mybe.module.email;

import de.arvato.mybe.backend.util.transaction.TransactionalByException;
import de.arvato.mybe.common.EmailQueueStatusType;
import de.arvato.mybe.dao.EmailMigrationDTO;
import de.arvato.mybe.dao.EmailQueueDAO;
import de.arvato.mybe.dao.EmailQueueDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailQueueService
{

    @PostConstruct
    @TransactionalByException
    public void cleanup() {
        //hopefully we only need to do this one time.
        EmailMigrationDTO emailMigrationDTO = emailQueueDAO.getLatestEmailMigration();

        if(emailMigrationDTO == null) {
            emailQueueDAO.updatePendingEmailQueueToCancel(1);
            List<EmailQueueDTO> cancelledEmail = emailQueueDAO.getEmailByStatus(EmailQueueStatusType.CANCELLED);

            long lastEmailId = 1;
            if(!cancelledEmail.isEmpty()){
                lastEmailId = cancelledEmail.get(cancelledEmail.size()-1).getId();
            }

            emailMigrationDTO = new EmailMigrationDTO();
            emailMigrationDTO.setLastEmailId(lastEmailId);
            emailQueueDAO.createEmailMigration(emailMigrationDTO);
        }
    }

    private final EmailQueueDAO emailQueueDAO;

    public List<EmailQueueDTO> getAllEmail()
    {
        return emailQueueDAO.getAllEmail();
    }

    public List<EmailQueueDTO> getEmailByStatus(String status)
    {
        return emailQueueDAO.getEmailByStatus(status);
    }

    public long create(EmailQueueDTO dto)
    {
        return emailQueueDAO.create(dto);
    }

    public void update(long emailQueueId, EmailQueueDTO dto)
    {
        emailQueueDAO.update(emailQueueId, dto);
    }
}
