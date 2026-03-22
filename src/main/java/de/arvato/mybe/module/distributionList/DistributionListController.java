package de.arvato.mybe.module.distributionList;

import aep.core.starter.security.methodsecurity.HasRight;
import de.arvato.mybe.backend.rest.RemoteResponse;
import de.arvato.mybe.dao.DistributionListDTO;
import de.arvato.mybe.module.employee.EmployeeService;
import de.arvato.mybe.module.orgunit.OrgUnitService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/email-distribution")
public class DistributionListController {

    private final DistributionListService distributionListService;

    private final EmployeeService employeeService;

    private final OrgUnitService orgUnitService;

    @GetMapping
    @ResponseBody
    @Operation(
            summary = "Get all email distribution list",
            description = "Get all email distribution list"
    )
    @HasRight("central.distributionlist.view")
    public RemoteResponse getDistributionList(){
        return RemoteResponse.fillResponseSuccess(distributionListService.getAllDistributionList());
    }

    @GetMapping(path = "/{id}")
    @ResponseBody
    @Operation(
            summary = "Get email distribution list",
            description = "Get email distribution list by id"
    )
    @HasRight("central.distributionlist.view")
    public RemoteResponse getDistributionListById(@PathVariable long id){
        return RemoteResponse.fillResponseSuccess(distributionListService.getDistributionListById(id));
    }

    @PostMapping
    @ResponseBody
    @Operation(
            summary = "Add email distribution list",
            description = "Add email distribution list"
    )
    @HasRight("central.distributionlist.edit")
    public RemoteResponse addDistributionList(@RequestBody DistributionListDTO distributionList){
        return RemoteResponse.fillResponseSuccess(distributionListService.createDistributionList(distributionList));
    }

    @PutMapping(path = "/{id}")
    @ResponseBody
    @Operation(
            summary = "Update email distribution list",
            description = "Update email distribution list"
    )
    @HasRight("central.distributionlist.edit")
    public RemoteResponse updateDistributionList(@PathVariable long id, @RequestBody DistributionListDTO distributionList){
        return RemoteResponse.fillResponseSuccess(distributionListService.updateDistributionList(id, distributionList));
    }

    @DeleteMapping(path = "/{id}")
    @ResponseBody
    @Operation(
            summary = "Delete email distribution list",
            description = "Delete email distribution list by id"
    )
    @HasRight("central.distributionlist.edit")
    public RemoteResponse deleteDistributionList(@PathVariable long id){
        return RemoteResponse.fillResponseSuccess(distributionListService.deleteDistributionList(id));
    }

    @GetMapping(path = "/employees")
    @ResponseBody
    @Operation(
            summary = "Get all employees for email distribution list",
            description = "Get all employees for email distribution list"
    )
    @HasRight("central.distributionlist.edit")
    public RemoteResponse getAllEmployees()
    {
        return RemoteResponse.fillResponseSuccess(employeeService.getAllEmployees().stream().filter(x -> x.isActive()).collect(Collectors.toList()));
    }

    @GetMapping(path = "/orgunit")
    @ResponseBody
    @Operation(
            summary = "Get all Organisation Units for email distribution list",
            description = "Get all Organisation Units for email distribution list")
    @HasRight("central.distributionlist.edit")
    public RemoteResponse getAllOrgUnit()
    {
        return RemoteResponse.fillResponseSuccess(orgUnitService.getAllOrgUnit());
    }

    @GetMapping(path = "/racf/{racf}")
    @ResponseBody
    @Operation(
            summary = "Get all Organisation Units for email distribution list",
            description = "Get all Organisation Units for email distribution list")
    @HasRight("central.distributionlist.edit")
    public RemoteResponse getDistributionListByRacf(@PathVariable String racf)
    {
        return RemoteResponse.fillResponseSuccess(distributionListService.getDistributionListByRacf(racf));
    }

    @GetMapping(path = "/export")
    @ResponseBody
    @Operation(
            summary = "Export distribution list",
            description = "Export distribution list")
    @HasRight("central.distributionlist.edit")
    public RemoteResponse exportDistributionList()
    {
        return RemoteResponse.fillResponseSuccess(Collections.singletonList(distributionListService.exportDistributionListCSV()));
    }
}
