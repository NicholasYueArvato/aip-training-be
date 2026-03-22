package de.arvato.mybe.module.email;

import de.arvato.mybe.common.EmailQueueStatusType;
import de.arvato.mybe.dao.EmailQueueDAO;
import de.arvato.mybe.dao.EmailQueueDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmailQueueScheduler
{
    private final EmailQueueDAO emailQueueDAO;
    private final SendService sendService;

    @Scheduled(cron = "${email.queue.scheduler.cron}")
    public void execute()
    {
        emailQueueDAO.getEmailByStatus(EmailQueueStatusType.PENDING).forEach(this::sendEmail);
    }

    public void sendEmail(EmailQueueDTO queue)
    {
        sendService.sendEmail(queue);
    }

}
