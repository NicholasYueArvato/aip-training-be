package de.arvato.mybe.module.email;

import de.arvato.mybe.common.EmailQueueStatusType;
import de.arvato.mybe.common.EmailQueueType;
import de.arvato.mybe.dao.EmailQueueDAO;
import de.arvato.mybe.dao.EmailQueueDTO;
import de.arvato.mybe.dao.EmployeeDAO;
import de.arvato.mybe.dao.EmployeeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(prefix = "bep.central.new-employee-notification", name = "enabled", havingValue = "true")
public class NewEmployeeEmailScheduler
{
    @Value("${bep.central.new-employee-notification.window.minutes}")
    private Integer definedMinutes;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${bep.central.new-employee-notification.recipient}")
    private String recipient;

    @Value("${bep.central.new-employee-notification.subject}")
    private String subject;

    @Value("classpath:email-templates/newEmployeeEmailTemplate.txt")
    Resource resourceFile;

    private final EmployeeDAO employeeDAO;
    private final EmailQueueDAO emailQueueDAO;



    @Scheduled(cron = "${bep.central.new-employee-notification.cron}")
    @Transactional
    public void execute()
    {
        log.info("NewEmployeeEmailScheduler :: Checking employee");
        Date definedDate = new Date(System.currentTimeMillis() - definedMinutes * 60 * 1000);

        List<EmployeeDTO> employeeList = employeeDAO.getEmployeeByDefDateAndCheckEmailSend(definedDate, false);
        if (employeeList != null && !employeeList.isEmpty()) {
            String content = buildEmailTemplate(employeeList);

            EmailQueueDTO emailQueueDTO = new EmailQueueDTO();
            emailQueueDTO.setSender(senderEmail);
            emailQueueDTO.setRecipient(recipient);
            emailQueueDTO.setSubject(subject);
            emailQueueDTO.setContents(content);
            emailQueueDTO.setStatus(EmailQueueStatusType.PENDING);
            emailQueueDTO.setType(EmailQueueType.HTML);
            emailQueueDAO.create(emailQueueDTO);

            employeeDAO.updateEmployeeCheckEmailSend(getEmployeeIDList(employeeList));
        }
    }

    public String buildEmailTemplate(List<EmployeeDTO> employeeList)
    {
        int serialNum = 1;
        StringBuilder template = new StringBuilder();
        for (EmployeeDTO employee : employeeList) {
            StringBuilder rowBuilder = new StringBuilder().append("<tr>");
            StringBuilder columnBuilder = new StringBuilder("<td>")
                    .append(serialNum++)
                    .append("</td>");
            rowBuilder.append(columnBuilder);
            columnBuilder = new StringBuilder("<td>")
                    .append(employee.getFirstName())
                    .append(" ")
                    .append(employee.getLastName())
                    .append("</td>");
            rowBuilder.append(columnBuilder);
            columnBuilder = new StringBuilder("<td>")
                    .append(employee.getRacf())
                    .append("</td>");
            rowBuilder.append(columnBuilder);
            columnBuilder = new StringBuilder("<td>")
                    .append(employee.getEmail())
                    .append("</td>");
            rowBuilder.append(columnBuilder);
            columnBuilder = new StringBuilder("<td>")
                    .append(formatDate(employee.getJoiningDate()))
                    .append("</td>");
            rowBuilder.append(columnBuilder);
            columnBuilder = new StringBuilder("<td>")
                    .append(employee.getSupervisorName() != null ? employee.getSupervisorName() : "N/A")
                    .append("</td>");
            rowBuilder.append(columnBuilder).append("</tr>");

            template.append(rowBuilder);
        }
        return formatEmailTagFromResource(template.toString());
    }

    private String formatEmailTagFromResource(String template) {
        File file;
        String content;
        try {
            file = resourceFile.getFile();
            content = new String(Files.readAllBytes(file.toPath()));
            if (!content.isEmpty()) {
                content = content.replace("${Title}", subject);
                content = content.replace("${EmployeeData}", template);
            }
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
        return dt1.format(date);
    }

    private List<Long> getEmployeeIDList(List<EmployeeDTO> employeeList) {
        return employeeList.stream().map(EmployeeDTO::getId).collect(Collectors.toList());
    }
}
