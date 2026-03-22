package de.arvato.mybe.module.sitemanagement;

import de.arvato.mybe.backend.rest.RemoteResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sites")
public class SiteManagerController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SiteManagerController.class);

    @Autowired
    private SiteService siteService;



    @GetMapping(path = "/active")
    @PreAuthorize("hasAuthority('MANAGE_SITES')")
    @ResponseBody
    @Operation(
            summary = "Get all active sites",
            description = "Get all active sites"
    )
    public RemoteResponse getSites()
    {
        return RemoteResponse.fillResponseSuccess(siteService.findActiveSites());
    }
}
