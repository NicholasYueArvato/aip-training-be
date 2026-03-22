package de.arvato.mybe.module.employee;

import aep.core.starter.security.methodsecurity.HasRight;
import de.arvato.mybe.backend.general.ErrorCodes;
import de.arvato.mybe.backend.rest.RemoteResponse;
import de.arvato.mybe.dao.EmployeeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "employee-api")
@RestController
@RequestMapping("/api/employee")
public class EmployeeController
{
    private final EmployeeService employeeService;

    @GetMapping(path = "/{id}")
    @ResponseBody
    @Operation(
            summary = "Get employee",
            description = "Get employee by id"
    )
    @HasRight("central.employee.view")
    public RemoteResponse getEmployee(@PathVariable long id)
    {
        return RemoteResponse.fillResponseSuccess(employeeService.getEmployee(id));
    }

    @GetMapping
    @ResponseBody
    @Operation(
            summary = "Get all employees",
            description = "Get all employees"
    )
    @HasRight("central.employee.view")
    public RemoteResponse getAllEmployees()
    {
        return RemoteResponse.fillResponseSuccess(employeeService.getAllEmployees());
    }

    @GetMapping("/business-unit")
    @ResponseBody
    @Operation(
            summary = "Get all employees by BU short name",
            description = "Get all employees by BU short name"
    )
    @HasRight("central.employee.view")
    public RemoteResponse getEmployeesByBuShortName(@RequestParam List<String> shortNameList)
    {
        return RemoteResponse.fillResponseSuccess(employeeService.getEmployeesByBU(shortNameList));
    }

    @PostMapping
    @ResponseBody
    @Operation(
            summary = "Add employee",
            description = "Add employee"
    )
    @HasRight("central.employee.edit")
    public RemoteResponse createEmployee(@RequestBody EmployeeDTO employee)
    {
        return RemoteResponse.fillResponseSuccess(employeeService.createEmployee(employee) > 0);
    }

    @PutMapping(path = "/{id}")
    @ResponseBody
    @Operation(
            summary = "Update employee",
            description = "Update employee"
    )
    @HasRight("central.employee.edit")
    public RemoteResponse updateEmployee(@PathVariable long id, @RequestBody EmployeeDTO employee)
    {
        return RemoteResponse.fillResponseSuccess(employeeService.updateEmployee(id, employee));
    }

    @DeleteMapping(path = "/{id}")
    @ResponseBody
    @Operation(
            summary = "Delete employee",
            description = "Delete employee by id"
    )
    @HasRight("central.employee.edit")
    public RemoteResponse deleteEmployee(@PathVariable long id)
    {
        return RemoteResponse.fillResponseSuccess(employeeService.deleteEmployeeById(id));
    }

    @PostMapping(path = "/import")
    @ResponseBody
    @Operation(
            summary = "Import employee",
            description = "Import employee"
    )
    @HasRight("central.employee.edit")
    public RemoteResponse uploadFile(@RequestParam("file") MultipartFile multipartFile, @RequestParam("replaceSupervisor") boolean replaceSupervisor)
    {
        try
        {
            employeeService.saveUploadFile(multipartFile.getOriginalFilename(), multipartFile.getInputStream(), replaceSupervisor);
            return RemoteResponse.fillResponseSuccess();
        }
        catch (Exception ex)
        {
            log.error("Error in importing employee data, {}", ex.getMessage(), ex);
            return RemoteResponse.fillResponseFailed(ErrorCodes.GENERAL_EXCEPTION, ex.getMessage());
        }
    }
    @GetMapping("/export")
    @ResponseBody
    @Operation(
            summary = "Get all employees data in csv format",
            description = "Get all employees data in csv format"
    )
    @HasRight("central.employee.view")
    public RemoteResponse exportEmployeeData()
    {
        return RemoteResponse.fillResponseSuccess(Collections.singletonList(employeeService.exportEmployeeData()));
    }


}
