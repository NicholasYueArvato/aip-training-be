package de.arvato.mybe.module.email;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import de.arvato.mybe.common.EmailQueueStatusType;
import de.arvato.mybe.common.EmailQueueType;
import de.arvato.mybe.dao.EmailQueueDAO;
import de.arvato.mybe.dao.EmailQueueDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SendService
{
    private final EmailQueueDAO emailQueueDAO;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${spring.mail.sendername}")
    private String senderName;

    private final JavaMailSender mailService;

    @Retryable(value = Exception.class, maxAttemptsExpression = "${sendMail.retry.max.attempts}")
    public void sendEmail(EmailQueueDTO queue)
    {
        if (queue.getType() != null && queue.getType().equals(EmailQueueType.HTML)) {
            sendHTMLEmail(queue);
        } else {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderName);
            message.setTo(queue.getRecipient());
            message.setSubject(queue.getSubject());
            message.setText(queue.getContents());
            if (CollectionUtils.isNotEmpty(queue.getCc())) {
                message.setCc(queue.getCc().toArray(new String[0]));
            }
            mailService.send(message);
        }
        emailQueueDAO.updateStatus(queue.getId(), EmailQueueStatusType.COMPLETED);
    }

    private void sendHTMLEmail(EmailQueueDTO queue) {
        try {
            MimeMessage message = mailService.createMimeMessage();
            message.setFrom(senderName);
            message.setRecipients(MimeMessage.RecipientType.TO, queue.getRecipient());
            message.setSubject(queue.getSubject());
            message.setContent(queue.getContents(), "text/html; charset=utf-8");
            if (CollectionUtils.isNotEmpty(queue.getCc())) {
                for (int i = 0; i < queue.getCc().size(); i++) {
                    message.addRecipients(MimeMessage.RecipientType.CC, queue.getCc().get(i));
                }
            }
            mailService.send(message);
        } catch (MessagingException e) {
            log.info("Error in send html mail, {}", e);
            throw new RuntimeException(e);
        }
    }

    @Recover
    public void emailQueueError(RuntimeException ex, EmailQueueDTO queue)
    {
        log.info("Error in mail queue, {}", ex);
        emailQueueDAO.updateStatus(queue.getId(), EmailQueueStatusType.ERROR);
    }
}
