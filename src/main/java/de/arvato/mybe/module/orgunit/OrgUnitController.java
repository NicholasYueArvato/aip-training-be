package de.arvato.mybe.module.orgunit;

import java.util.Collections;

import de.arvato.mybe.backend.rest.RemoteResponse;
import de.arvato.mybe.dao.OrgUnitDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "organisation-unit-api")
@RestController
@RequestMapping("/api/orgUnit")
public class OrgUnitController
{
    private final OrgUnitService orgUnitService;

    @GetMapping(path = "/{id}")
    @ResponseBody
    @Operation(summary = "Get Organisation Unit", description = "Get Organisation Unit by ID")
    // @HasRight("central.org.view")
    public RemoteResponse getOrgUnit(@PathVariable long id)
    {
        return RemoteResponse.fillResponseSuccess(Collections.singletonList(orgUnitService.getByOrgUnitId(id)));
    }

    @GetMapping
    @ResponseBody
    @Operation(summary = "Get all Organisation Units", description = "Get all Organisation Units")
    //@HasRight("central.org.view")
    public RemoteResponse getAllOrgUnit()
    {
        return RemoteResponse.fillResponseSuccess(orgUnitService.getAllOrgUnit());
    }

    @PostMapping
    @ResponseBody
    @Operation(summary = "Add Organisation Unit", description = "Add Organisation Unit")
    // @HasRight("central.org.edit")
    public RemoteResponse addOrgUnit(@RequestBody OrgUnitDTO orgUnit)
    {
        return RemoteResponse.fillResponseSuccess(orgUnitService.createOrgUnit(orgUnit) > 0);
    }

    @PutMapping(path = "/{id}")
    @ResponseBody
    @Operation(summary = "Update Organisation Unit", description = "Update Organisation Unit")
    //  @HasRight("central.org.edit")
    public RemoteResponse updateOrgUnit(@PathVariable long id, @RequestBody OrgUnitDTO orgUnit)
    {
        return RemoteResponse.fillResponseSuccess(orgUnitService.updateOrgUnit(id, orgUnit));
    }

    @DeleteMapping(path = "/{id}")
    @ResponseBody
    @Operation(summary = "Delete Organisation Unit", description = "Delete Organisation Unit by id")
    //  @HasRight("central.org.edit")
    public RemoteResponse deleteOrgUnit(@PathVariable long id)
    {
        return RemoteResponse.fillResponseSuccess(orgUnitService.deleteByOrgUnitId(id));
    }
}
