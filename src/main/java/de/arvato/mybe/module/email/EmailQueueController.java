package de.arvato.mybe.module.email;

import de.arvato.mybe.backend.rest.RemoteResponse;
import de.arvato.mybe.dao.EmailQueueDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "email-queue-api")
@RestController
@RequestMapping("/api/emailQueue")
public class EmailQueueController
{
    private final EmailQueueService emailQueueService;

    @GetMapping
    @ResponseBody
    @Operation(
            summary = "Get email queue",
            description = "Get all email queue"
    )
    public RemoteResponse getAllEmailQueue()
    {
        return RemoteResponse.fillResponseSuccess(emailQueueService.getAllEmail());
    }

    @GetMapping(path = "/{status}")
    @ResponseBody
    @Operation(
            summary = "Get email queue",
            description = "Get email queue with specific status"
    )
    public RemoteResponse getEmailQueueByStatus(@PathVariable String status)
    {
        return RemoteResponse.fillResponseSuccess(emailQueueService.getEmailByStatus(status));
    }

    @PostMapping
    @ResponseBody
    @Operation(
            summary = "Add email queue",
            description = "Add email queue"
    )
    public RemoteResponse addEmailQueue(@RequestBody EmailQueueDTO dto)
    {
        emailQueueService.create(dto);
        return RemoteResponse.fillResponseSuccess();
    }
}
